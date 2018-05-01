precision mediump float;

uniform sampler2D textureUnit0;

varying vec2 texCoord;
varying vec4 vertexColor;

void main() {
	gl_FragColor= vertexColor;// texture2D(textureUnit0, texCoord) * vertexColor;
}

