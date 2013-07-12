// # pragma once

#ifndef _COORDS_H_
#define _COORDS_H_

#include "../../../juce.h"

class Coords
{
public:
    Coords();

    Coords (const Coords& other);

    Coords (const int x, const int y);

    const Coords& operator= (const Coords& other);

    bool operator== (const Coords& other);

    ~Coords();

    //inline int getX() const throw()
    //{
    //    return x;
    //}

    //inline int getY() const throw()
    //{
    //    return y;
    //}

    void setXY (const int x, const int y);

    const String toXYString();

    // ------
    int x;
    int y;
};

#endif