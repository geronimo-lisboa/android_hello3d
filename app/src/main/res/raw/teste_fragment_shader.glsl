precision mediump float;
uniform mat4 modelMatrix;
uniform vec3 lightPositions[8];
uniform vec3 cameraPosition;
uniform float borderCoefficent;

varying vec3 vNormal;
varying vec3 vPosition;

void main() {
    //A posição do fragmento no mundo
    vec3 worldPosition = ( modelMatrix * vec4(vPosition, 1.0)).xyz;
    //Transformando a normal
    vec3 worldNormal = normalize(vec3(modelMatrix * vec4(vNormal,1.0)));
    //vetor da fonte da luz pra posição espacial do vertice
    vec3 lightPosition = lightPositions[0];
    vec3 lightVector = normalize(lightPosition-worldPosition);
    //o vetor da posição da câmera para a posição do fragmento
    vec3 camVector = normalize(cameraPosition - worldPosition);
    float relationBetweenFragAndCam = max(0.0, dot(camVector, worldNormal));
    vec3 colors[8];
    if (relationBetweenFragAndCam < borderCoefficent){
        gl_FragColor = vec4(0,0,0,1);//preto
    }
    else{
        for(int i=0; i<8; i++){
            //Função de transferência da cor de toon. De acordo com o angulo entre a
            //normal e a luz escolhe-se um valor dessa tabela para ser o brilho
            float ToonThresholds[4];
            ToonThresholds[0] = 0.95;
            ToonThresholds[1] = 0.5;
            ToonThresholds[2] = 0.2;
            ToonThresholds[3] = 0.03;
            float ToonBrightnessLevels[5];
            ToonBrightnessLevels[0] = 1.0;
            ToonBrightnessLevels[1] = 0.8;
            ToonBrightnessLevels[2] = 0.6;
            ToonBrightnessLevels[3] = 0.35;
            ToonBrightnessLevels[4] = 0.0;
            //Calculo do brilho
            float brightness = dot( worldNormal, lightVector );
            //Aplicação da função de transferência
            vec3 _color = vec3(0.5,0.3, 0.0);
            if (brightness > ToonThresholds[0]) {
                _color *= ToonBrightnessLevels[0];
            } else if (brightness > ToonThresholds[1])  {
                _color *= ToonBrightnessLevels[1];
            } else if (brightness > ToonThresholds[2]) {
                _color *= ToonBrightnessLevels[2];
            } else if (brightness > ToonThresholds[3]) {
                _color *= ToonBrightnessLevels[3];
            } else {
                _color *= ToonBrightnessLevels[4];
            }
            colors[i] = _color;
        }
        vec3 chosenColor = vec3(0,0,0);
        for(int i=0; i<8; i++){
            chosenColor = max(chosenColor, colors[i]);
        }
        //pronto
        gl_FragColor = vec4( chosenColor, 1.0 );
    }
}

