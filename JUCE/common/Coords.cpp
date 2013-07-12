#include "Coords.h"

Coords::Coords()
   : x (0),
     y (0)
{
}

Coords::Coords (const Coords& other)
   : x (other.x),
     y (other.y)
{
}

const Coords& Coords::operator= (const Coords& other)
{
    x = other.x;
    y = other.y;

    return *this;
}


bool Coords::operator== (const Coords& other)
{
    if ( (x == other.x) && 
         (y == other.y) ) return true;

    return false;
}

Coords::Coords (const int x_, const int y_)
   : x (x_),
     y (y_)
{
}

Coords::~Coords()
{
}

void Coords::setXY (const int x_, const int y_)
{
    x = x_;
    y = y_;
}

const String Coords::toXYString()
{
    return T("[")+String(x)+T(",")+String(y)+T("]");
}