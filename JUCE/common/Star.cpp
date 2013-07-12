
#include "Star.h"
#include "../../../juce.h"


Star::Star() : x(0), y(0), c(Colours::black), hx(0), hy(0) {};

Star::Star(const Star& other) : x(other.x), y(other.y), c(other.c), hx(other.hx), hy(other.hy) {};

Star::Star(const int x_, const int y_) : x(x_), y(y_), c(Colours::grey), hx(0), hy(0) {};

Star::Star(const int x_, const int y_, const Colour& c_) : x(x_), y(y_), c(c_), hx(0), hy(0) {};

Star::Star(const int x_, const int y_, const Colour& c_, const int hx_, const int hy_) : x(x_), y(y_), c(c_), hx(hx_), hy(hy_) {};

Star::~Star() {};
