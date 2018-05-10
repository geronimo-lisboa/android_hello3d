precision mediump float;
uniform mat4 modelMatrix;
uniform vec3 lightPositions[8];
uniform vec3 cameraPosition;

uniform vec3 diffuse;

varying vec3 vNormal;
varying vec3 vPosition;

void main() {
    //A posição do fragmento no mundo
    vec3 worldPosition = ( modelMatrix * vec4(vPosition, 1.0)).xyz;
    //Transformando a normal
    vec3 worldNormal = normalize(vec3(modelMatrix * vec4(vNormal,1.0)));
    //vetor da luz pra posição espacial do ponto
    vec3 lightPosition = lightPositions[0];
    vec3 lightVector = normalize(lightPosition-worldPosition);
    //calcula o brilho
    float brightness = dot(worldNormal, lightVector);
    //calcula a cor
    vec3 outputColor = vec3(diffuse * brightness);

    gl_FragColor = vec4( outputColor, 1.0 );
//    vec3 wnAsColor = (worldNormal + vec3(1,1,1))*0.5;
//    gl_FragColor = vec4( wnAsColor, 1.0 );
}

