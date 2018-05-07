precision mediump float;
uniform mat4 modelMatrix;

varying vec3 vNormal;
varying vec3 vPosition;

void main() {
    //A posição do fragmento no mundo
    vec3 worldPosition = ( modelMatrix * vec4(vPosition, 1.0)).xyz;
	gl_FragColor= vec4(worldPosition,1);// texture2D(textureUnit0, texCoord) * vertexColor;
}

