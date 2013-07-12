
// # pragma once

#ifndef _STAR_H_
#define _STAR_H_

#include "../../../juce.h"

class Star
{
public:
    Star();
    Star(const Star& other);
    Star(const int x, const int y);
    Star(const int x, const int y, const Colour& c);
    Star(const int x, const int y, const Colour& c, const int hx, const int hy);
    ~Star();

    // ------
    int x;
    int y;
    Colour c;
    int hx;
    int hy;
};

#endif