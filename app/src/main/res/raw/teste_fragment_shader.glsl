precision mediump float;

uniform sampler2D textureUnit0;

varying vec2 texCoord;
varying vec4 vertexColor;

void main() {
	gl_FragColor= vec4(1,0,0,1);// texture2D(textureUnit0, texCoord) * vertexColor;
}

