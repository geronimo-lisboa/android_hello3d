precision mediump float;
uniform vec3 testLightPos;
uniform mat4 modelMatrix;
uniform vec3 lightPositions[8];
uniform vec3 cameraPosition;

uniform float seaLevel;

varying vec3 vNormal;
varying vec3 vPosition;
/*De acordo com o ângulo entre a normal do fragmento e a normal de um terrno totalmente plano (0,1,0)
eu faço um blending entre a cor de um terreno plano e o terreno de pedreira.*/
vec3 evaluateTerrainByGradient(vec3 fragNormal){
    vec3 defaultNormal = normalize(vec3(modelMatrix * vec4(vec3(0,1,0),1.0)));
    float cosBetweenDefaultAndActual = dot(defaultNormal, fragNormal);
    vec3 pedraColor = vec3(0.75, 0.75, 0.75);
    vec3 gramaColor = vec3(0.35, 0.45, 0.01);

    return pedraColor * (1.0-cosBetweenDefaultAndActual) + gramaColor * cosBetweenDefaultAndActual;
}

vec3 evaluateTerrainByHeight(vec3 fragmentPos, vec3 fragmentCurrentColor){
    if(fragmentPos.y < seaLevel){
        fragmentCurrentColor.b = 1.0;
        return normalize(fragmentCurrentColor);
    }else{
        return fragmentCurrentColor;
    }
}

void main() {
    vec3 worldPosition = ( modelMatrix * vec4(vPosition, 1.0)).xyz;
    vec3 worldNormal =  normalize(vec3(modelMatrix * vec4(vNormal,1.0)));
    vec3 correctedLightPos = testLightPos;
    correctedLightPos.y = -correctedLightPos.y;
    vec3 lightVector = normalize(correctedLightPos-worldPosition);
    float brightness = dot(lightVector, worldNormal);

    vec3 terrainColor = evaluateTerrainByGradient(worldNormal);
    terrainColor = evaluateTerrainByHeight(worldPosition, terrainColor);

    gl_FragColor = vec4(terrainColor * brightness, 1);
}