#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;

smooth out vec4 light;

// vertex position model to view matrix
uniform mat4 modelToViewMatrix;

uniform mat4 worldToViewMatrix;

// vertex normal model to view matrix
uniform mat4 modelToViewNormalMatrix;

// vertex position view to perspective matrix
uniform mat4 viewToPerspectiveMatrix;

struct PointLight {
  vec3 worldPosition;
  vec3 color;
  float maxDistance;
};

float applyPointLight(mat4 viewMatrix, vec3 vertexViewPosition, vec3 vertexViewNormal, PointLight pointLight) {

  vec3 pointLightViewPosition = (viewMatrix * vec4(pointLight.worldPosition, 1.0)).xyz;
  vec3 vertexToLight = pointLightViewPosition-vertexViewPosition;
  float distanceToLight = length(vertexToLight);

  if(distanceToLight <= pointLight.maxDistance) { // if the light is in range
    //float attenuation = 1.0 / (distanceToLight * distanceToLight);
    vec3 vertexToLightNormal = normalize(vertexToLight);
    float diffuse = dot(vertexToLightNormal, vertexViewNormal) * length(pointLight.color); // * attenuation;
    return max(diffuse, 0.0);
  } else {
    return 0.0;
  }
}

void main()
{
	vec4 viewPosition = modelToViewMatrix * vec4(position, 1.0);

	//mat4 normalMatrix = transpose(inverse(modelToViewMatrix));

	//vec3 viewNormal = normalize(worldToViewNormalMatrix * normal);
	vec3 viewNormal = normalize(modelToViewNormalMatrix * vec4(normal, 0.0)).xyz;

    PointLight pointLight = PointLight(vec3(5.0, 0.0, 5.0), vec3(1.0, 1.0, 1.0), 100.0);
    float lightIntensity = applyPointLight(worldToViewMatrix, viewPosition.xyz, viewNormal, pointLight);

    //vec4 ambientLight = vec4(0.0, 0.0, 0.2, 1.0);

    light = vec4(lightIntensity, lightIntensity, lightIntensity, 1.0);// + ambientLight;

    vec4 perspectivePosition = viewToPerspectiveMatrix * viewPosition;
    gl_Position = perspectivePosition;
}
