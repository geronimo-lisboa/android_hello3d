precision mediump float;
uniform vec3 testLightPos;
uniform mat4 modelMatrix;
uniform vec3 lightPositions[8];
uniform vec3 cameraPosition;
uniform float seaLevel;

varying vec3 vNormal;
varying vec3 vPosition;

vec3 evalShadingByInclination(vec3 normal){
    vec3 horizontalPlaneNormal =  normalize(vec3(modelMatrix * vec4(vec3(0,1,0),1.0)));
    float inclination = dot(horizontalPlaneNormal, normal);
    vec3 pedreiraColor = vec3(0.65, 0.64, 0.65);
    vec3 gramaColor = vec3(0.43, 0.62, 0.01);
    return pedreiraColor * (1.0 - inclination) + gramaColor * inclination;
}

vec3 evalShadingByHeight(vec3 worldPosition, vec3 currentColor){
    float height = worldPosition.y;
    if(height < seaLevel - 0.05){
        currentColor.b = 1.0;
        return normalize(currentColor);
    }
    else if(height < seaLevel + 0.05){
        currentColor = vec3(0.9,0.9, 0.5);
        return currentColor;
    }
    else{
        return currentColor;
    }
}

void main() {
    vec3 worldPosition = ( modelMatrix * vec4(vPosition, 1.0)).xyz;
    vec3 worldNormal =  normalize(vec3(modelMatrix * vec4(vNormal,1.0)));
    vec3 correctedLightPos = testLightPos;
    correctedLightPos.y = -correctedLightPos.y;
    vec3 lightVector = normalize(correctedLightPos-worldPosition);
    float brightness = dot(lightVector, worldNormal);
    //A cor segundo gradiente e altitude
    vec3 color = evalShadingByInclination(worldNormal);
    color = evalShadingByHeight(vPosition, color);
    //Aplica a iluminação.
    color = color * brightness;
    gl_FragColor = vec4(color,1);
}