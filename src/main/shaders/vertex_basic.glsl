#version 330
layout(location = 0) in vec3 position;
//layout(location = 1) in vec4 color;

smooth out vec4 theColor;
uniform vec2 offset;

void main()
{
  gl_Position = vec4(position + offset, 0.0, 0.0);
  theColor = vec4(1.0, 1.0, 1.0, 1.0);
}