#version 330
smooth in vec4 light;
out vec4 outputColor;
void main()
{
  outputColor = light;
}
