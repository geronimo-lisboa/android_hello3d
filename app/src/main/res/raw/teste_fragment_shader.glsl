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
    vec3 wnAsColor = (worldNormal + vec3(1,1,1))*0.5;
    gl_FragColor = vec4( wnAsColor, 1.0 );


}

