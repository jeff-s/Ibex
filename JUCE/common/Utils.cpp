#include <stdlib.h>
#include "Utils.h"

#define PACKBASE 64
#define PACKBASEQ 4160

Utils::Utils(void)
{
}

Utils::~Utils(void)
{
}

const String Utils::Now()
{
    return Time::getCurrentTime().toString(true,true);
}

const String Utils::net()
{
    URL u = URL(T("http://google.com/"));
    return u.readEntireTextStream();
}

const void Utils::mapper(Image& m)
{
        Graphics mapg (m);
        mapg.setColour(Colours::aquamarine);
        mapg.fillEllipse(50.0, 50.0, 50.0, 25.0);
}


// ------

Theme::Theme(void)
{}

Theme::~Theme(void)
{}

// ------

ProdEvent::ProdEvent(int index_, const String& where_, const String& what_, int qty_) :
    index(index_),where(where_),what(what_),qty(qty_)
    {}
    
    
ProdEvent::~ProdEvent(void) {};

// ------

Entity::Entity(void) {}
Entity::Entity(Entity::EntityType etype_, int owner_, Image* img_, const Coords& coords_,
               const String& name_, const String& desc_, int desctype_, int movecost_) :
    etype(etype_), owner(owner_), img(img_), coords(coords_), 
    name(name_), desc(desc_), desctype(desctype_), movecost(movecost_)
    {}
Entity::~Entity(void) {}

// ------
const Coords HexDirection(const Coords& i, int dir)
{
    int nx=-1,ny=-1;
    switch(dir)
    {
    case 1: //ne
        nx=i.x+(i.y % 2);
        ny=i.y-1;
        break;
    case 2: //e
        nx=i.x+1;
        ny=i.y;
        break;
    case 3: //se
        nx=i.x+(i.y % 2);
        ny=i.y+1;
        break;
    case 4: //sw
        nx=i.x-(i.y+1) % 2;
        ny=i.y+1;
        break;
    case 5: //w
        nx=i.x-1;
        ny=i.y;
        break;
    case 6: //nw
        nx=i.x-(i.y+1) % 2;
        ny=i.y-1;
        break;
    default:
        nx=i.x;
        ny=i.y;
        break;
    }
    return Coords(nx,ny);
}

bool CoordsInBounds(const Coords& c)
{
    if (c.x<0) return false;
    if (c.y<0) return false;
    if (c.x>63) return false;
    if (c.y>63) return false;
    return true;
}

int HexDistGuess(const Coords& c1, const Coords& c2)
{
    return HexDistGuess(c1.x,c1.y,c2.x,c2.y);
}

int HexDistGuess(int n1, int n2)
{
    return HexDistGuess(packx(n1),packy(n1),packx(n2),packy(n2));
}

int HexDistGuess(int x1, int y1, int x2, int y2)
{
    int dx=x1-x2;
    int dy=y1-y2;
    return ( (abs(dx)+abs(dy)+abs(dx-dy)) /2 );
}

int pack(int x, int y)
{
    if (x<0) x=PACKBASE-1;
    if (y<0) y=PACKBASE-1;
    return (x * PACKBASE + y);
}

int packx(int n) { return n/PACKBASE; }

int packy(int n) { return n%PACKBASE; }

const Colour RandomGray()
{
    unsigned char i= juce::Random::getSystemRandom().nextInt(50)+140;
    return Colour(i,i,i);
}

const String ParseTurn(const String& r, const String& t)
{
    if (!r.startsWith(T("[Ibex]")))
        return T("malformed turn file");

    int p1=r.indexOf(t);
    if (p1==-1)
        return String::empty; //tag not found

    int p2=r.indexOf(p1,T("="));
    if (p2==-1)
        return String::empty; //equals sign not found

    int p3=r.indexOfChar(p2,(char)10);

    return r.substring(p2+1,p3);
}

const String AppDir()
{
    return File::getSpecialLocation(File::currentApplicationFile).getFullPathName().replaceCharacter('\\','/').upToLastOccurrenceOf(T("/"), true, true);
}

const String ExeDir()
{
    return File::getSpecialLocation(File::currentExecutableFile).getFullPathName().replaceCharacter('\\','/').upToLastOccurrenceOf(T("/"), true, true);
}

int Rand(int lo, int hi)
{
    return juce::Random::getSystemRandom().nextInt(hi-lo)+lo;
}