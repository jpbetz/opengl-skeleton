#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;

smooth out vec4 theColor;

// normal model view to camera view matrix
uniform mat3 normalModelToCameraMatrix;

// vertex model view to camera view matrix
uniform mat4 cameraViewMatrix;

// camera view to perspective view matrix
uniform mat4 perspectiveMatrix;

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
	vec4 modelView = vec4(position, 1.0); // TODO: add a model view matrix transform
	vec4 cameraView = cameraViewMatrix * modelView;
	vec4 perspectiveView = perspectiveMatrix * cameraView;
	gl_Position = perspectiveView;


	vec3 vertexViewNormal = normalize(normalModelToCameraMatrix * normal);

    PointLight pointLight = PointLight(vec3(1.0, 1.0, 1.0), vec3(1.0, 1.0, 1.0), 100.0);
    float lightIntensity = applyPointLight(cameraViewMatrix, perspectiveView.xyz, vertexViewNormal, pointLight);

	theColor = vec4(lightIntensity, lightIntensity, lightIntensity, 1.0);
	//theColor = vec4(1.0, 1.0, 1.0, 1.0);
}
