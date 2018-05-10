uniform mat4 modelMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 modelViewProjectionMatrix;
uniform mat4 textureMatrix;

uniform vec4 additionalColor;
uniform vec4 ambientColor;

uniform float alpha;
uniform float shininess;
uniform bool useColors;

uniform int lightCount;

uniform vec3 lightPositions[8];
uniform vec3 diffuseColors[8];
uniform vec3 specularColors[8];
uniform float attenuation[8];

attribute vec4 position;
attribute vec3 normal;
attribute vec4 color;
attribute vec2 texture0;

varying vec4 vertexColor;
varying vec3 vNormalEye;
varying vec3 vPosition;

const vec4 WHITE = vec4(1,1,1,1);

void main() {
    vNormalEye = normalize(modelViewMatrix * vec4(normal,0.0)).xyz;

    gl_Position = modelViewProjectionMatrix * position;
    vPosition = position.xyz;
}