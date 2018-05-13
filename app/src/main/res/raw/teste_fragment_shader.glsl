precision mediump float;
uniform vec3 testLightPos;
uniform mat4 modelMatrix;
uniform vec3 lightPositions[8];
uniform vec3 cameraPosition;

varying vec3 vNormal;
varying vec3 vPosition;

float evaluateTerrainByGradient(vec3 fragNormal){
    vec3 defaultNormal = normalize(vec3(modelMatrix * vec4(vec3(0,1,0),1.0)));
    float cosBetweenDefaultAndActual = dot(defaultNormal, fragNormal);
    return cosBetweenDefaultAndActual;
}

void main() {
    vec3 worldPosition = ( modelMatrix * vec4(vPosition, 1.0)).xyz;
    vec3 worldNormal =  normalize(vec3(modelMatrix * vec4(vNormal,1.0)));
    vec3 correctedLightPos = testLightPos;
    correctedLightPos.y = -correctedLightPos.y;
    vec3 lightVector = normalize(correctedLightPos-worldPosition);
    float brightness = dot(lightVector, worldNormal);

    float normalAngle = evaluateTerrainByGradient(worldNormal);
    if(normalAngle < 0.5){
        vec3 color = vec3(0.75, 0.75, 0.75);
        color = color * brightness;
        gl_FragColor = vec4(color,1);
    }
    else{
        vec3 color = vec3(0.25, 0.5, 0.01);
        color = color * brightness;
        gl_FragColor = vec4(color,1);
    }
}