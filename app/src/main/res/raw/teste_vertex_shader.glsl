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

varying vec2 texCoord;
varying vec4 vertexColor;

const vec4 WHITE = vec4(1,1,1,1);

void main() {

	texCoord = (textureMatrix * vec4(texture0, 0, 1)).xy;	

	vertexColor = ambientColor + additionalColor;

	if (lightCount>0) {
	
		vec4 vertexPos = modelViewMatrix * position;
	
		// This is correct only if the modelview matrix is orthogonal. In jPCT-AE, it always is...unless you fiddle around with it.
		vec3 normalEye   = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;
		
		vec3 surface2Light=normalize(lightPositions[0]  - vertexPos.xyz);
		float angle = dot(normalEye, surface2Light);
			
		if (angle > 0.0) {
			vertexColor += vec4((diffuseColors[0] * angle + specularColors[0] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[0] - vertexPos.xyz)*attenuation[0])), 1);
		}

		// Freaky Adreno shader compiler can't handle loops without locking or creating garbage results....this is why the
		// loop has been unrolled here. It's faster this way on PowerVR SGX540 too, even if PVRUniSCoEditor says otherwise... 

		if (lightCount>1) {
			surface2Light=normalize(lightPositions[1]  - vertexPos.xyz);
			angle = dot(normalEye, surface2Light);

			if (angle > 0.0) {
				vertexColor += vec4((diffuseColors[1] * angle + specularColors[1] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[1] - vertexPos.xyz)*attenuation[1])), 1);
			}

			if (lightCount>2) {
				surface2Light=normalize(lightPositions[2]  - vertexPos.xyz);
				angle = dot(normalEye, surface2Light);

				if (angle > 0.0) {
					vertexColor += vec4((diffuseColors[2] * angle + specularColors[2] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[2] - vertexPos.xyz)*attenuation[2])), 1);
				}

				if (lightCount>3) {
					surface2Light=normalize(lightPositions[3]  - vertexPos.xyz);
					angle = dot(normalEye, surface2Light);

					if (angle > 0.0) {
						vertexColor += vec4((diffuseColors[3] * angle + specularColors[3] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[3] - vertexPos.xyz)*attenuation[3])), 1);
					}

					if (lightCount>4) {
						surface2Light=normalize(lightPositions[4]  - vertexPos.xyz);
						angle = dot(normalEye, surface2Light);

						if (angle > 0.0) {
							vertexColor += vec4((diffuseColors[4] * angle + specularColors[4] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[4] - vertexPos.xyz)*attenuation[4])), 1);
						}

						if (lightCount>5) {
							surface2Light=normalize(lightPositions[5]  - vertexPos.xyz);
							angle = dot(normalEye, surface2Light);

							if (angle > 0.0) {
								vertexColor += vec4((diffuseColors[5] * angle + specularColors[5] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[5] - vertexPos.xyz)*attenuation[5])), 1);
							}

							if (lightCount>6) {
								surface2Light=normalize(lightPositions[6]  - vertexPos.xyz);
								angle = dot(normalEye, surface2Light);

								if (angle > 0.0) {
									vertexColor += vec4((diffuseColors[6] * angle + specularColors[6] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[6] - vertexPos.xyz)*attenuation[6])), 1);
								}
								if (lightCount>7) {
									surface2Light=normalize(lightPositions[7]  - vertexPos.xyz);
									angle = dot(normalEye, surface2Light);

									if (angle > 0.0) {
										vertexColor += vec4((diffuseColors[7] * angle + specularColors[7] * pow(max(0.0, dot(normalize(-vertexPos.xyz), reflect(-surface2Light, normalEye))), shininess))*(1.0/(1.0+length(lightPositions[7] - vertexPos.xyz)*attenuation[7])), 1);
									}
								}
							}
						}
					}
				}
			}
		}
	} 
	
	vertexColor=vec4(min(WHITE, vertexColor).xyz, alpha);
	
	if (useColors) {
		vertexColor *= color;
	}
	
	gl_Position = modelViewProjectionMatrix * position;
}