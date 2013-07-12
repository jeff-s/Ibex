
#ifndef _UTILS_H_
#define _UTILS_H_

// # pragma once

#include "../../../juce.h"
#include "Coords.h"

const String ParseTurn(const String& r, const String& t);

const Coords HexDirection (const Coords& i, int dir);
int HexDistGuess(const Coords& c1, const Coords& c2);
int HexDistGuess(int x1, int y1, int x2, int y2);
int HexDistGuess(int n1, int n2);
int pack(int x, int y);
int packx(int n);
int packy(int n);

bool CoordsInBounds(const Coords& c);

const Colour RandomGray ();

int Rand(int lo, int hi);

const String AppDir();
const String ExeDir();

class Utils
{
public:
    Utils(void);
    ~Utils(void);

    static const String Now();

    static const String net();

    static const void mapper(Image& m);

};

class Theme
{
public:
	Theme(void);
	~Theme(void);
	Colour bg;
	Colour button;
	Colour border;
	Colour borderhigh;
	Colour grid;
	Colour hexcursorcolor;
	Colour mapbg;
	Colour text;
    Colour maptext;
    Colour hexdisputedcolor;
    Colour hexanchorcolor;
    Colour hexpathcolor;
    Colour hexpathredcolor;
};

class ProdEvent
{
public:
    ProdEvent(int index,const String& where, const String& what, int qty);
    ~ProdEvent(void);
    int index;
    String where;
    String what;
    int qty;
};

class Entity
{
public:
    enum EntityType
    {
        player = 1,
        planet = 2,
        nebula = 3
    };

    EntityType etype;
    int owner;
    Image *img;
    Coords coords;
    String name;
    String desc; //was "class" in orig ibex
    int desctype; //was "classtype" in orig ibex
    int movecost;
    int frame;

    Entity(void);
    Entity(Entity::EntityType etype_, int owner_, Image* img_, const Coords& coords_, const String& name_, const String& desc_, int desctype_, int movecost_);
    ~Entity(void);
};

#endif