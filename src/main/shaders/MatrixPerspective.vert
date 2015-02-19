#version 330

layout(location = 0) in vec3 position;
//layout(location = 1) in vec4 color;

smooth out vec4 theColor;

uniform vec3 offset;
uniform mat4 perspectiveMatrix;

void main()
{
	vec4 cameraPos = vec4(position + offset, 1.0);
	gl_Position = perspectiveMatrix * cameraPos;
	theColor = vec4(1.0, 1.0, 1.0, 1.0);
}