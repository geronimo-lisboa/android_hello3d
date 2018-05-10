precision mediump float;
uniform mat4 modelMatrix;
uniform vec3 lightPositions[8];
uniform vec3 cameraPosition;

varying vec3 vNormal;
varying vec3 vPosition;

void main() {
    vec3 worldPosition = ( modelMatrix * vec4(vPosition, 1.0)).xyz;
    vec3 worldNormal =  normalize(vec3(modelMatrix * vec4(vNormal,1.0)));
    worldNormal.y = -worldNormal.y ;
    vec3 lightVector = normalize(lightPositions[0]-worldPosition);
    float brightness = dot(lightVector, worldNormal);
    vec3 color = vec3(0.5, 0.5, 0.01);
    color = color * brightness;
    gl_FragColor = vec4(color,1);


}