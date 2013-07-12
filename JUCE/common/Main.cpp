/*

 Ibex.  An on-line, turn-based, trans-dimensional power struggle.

 0030 : 2008-02-   Port BB logic to JUCE

*/

#include "../../../juce.h"
#include "Utils.h"
#include "Coords.h"
#include "Star.h"
#include "InfoComponent.h"
#include "MovementQueueComponent.h"

#define WORKCOLORS true
#define FILEGET true
#define IBEXVER "0030"
#define VIS true
// VIS=true, show everything; false, use visibility rules
#define PACKBASE 64
#define PACKBASEQ 4160
// change packbase also in utils.cpp

//---
class MapComponent : public Component, public Timer
{
public:
    MapComponent(Theme* t)
    {
		theme=t;
        //starmax=50;
        heximg=new Image(Image::ARGB,1,1,false); //justa dummy
        hexcursor=new Image(Image::ARGB,1,1,false); //justa dummy
        hexdisputed=new Image(Image::ARGB,1,1,false); //justa dummy
        nebulaimage=new Image(Image::ARGB,1,1,false); //justa dummy
        hexanchor=new Image(Image::ARGB,1,1,false); //justa dummy
        hexpath=new Image(Image::ARGB,1,1,false); //justa dummy
        hexpathred=new Image(Image::ARGB,1,1,false); //justa dummy
        hexmovestep=new Image(Image::ARGB,1,1,false); //justa dummy

        font=new Font(T("Verdana"),14.0,Font::bold);

        StainColor.add(new Colour(Colours::grey)); //dummy to make 1-based
        StainColor.add(new Colour(Colours::grey)); //gray eye guys
        StainColor.add(new Colour(Colours::green)); //green ophids
        StainColor.add(new Colour(Colours::teal)); //teal magi
        StainColor.add(new Colour(Colours::purple)); //purple dragons
        StainColor.add(new Colour(Colours::orange)); //orange newly-expanded

        anchor=Coords(-1,-1);
        cMoveIndex=-1;
        cMoveName.clear();
        cMover=String::empty;

        ReDoGFX(18.0);
        
        map=new Image(Image::ARGB,1,1,false); //justa dummy
        hexcursoropacity=0.5f; hexcursoropacityinc=0.05f;

        startTimer(200);
    }

    ~MapComponent()
    {
        Entities.clear();
        ShipImage.clear();
        PlanetImage.clear();
        Stars.clear();
        StainColor.clear();
        Stain.clear();
        deleteAllChildren();
        delete map;
        delete heximg;
        delete hexcursor;
        delete hexdisputed;
        delete font;
        delete nebulaimage;
        delete hexanchor;
        delete hexpath;
        delete hexpathred;
        delete hexmovestep;
    }

    void resized()
    {
        cw=this->getWidth();
        ch=this->getHeight();
        //Stars.clear();
        //if ((cw>15) && (ch>15))
        //    for (int m=0; m<starmax; m++)
        //        Stars.add(new Star(juce::Random::getSystemRandom().nextInt(cw-10)+5, juce::Random::getSystemRandom().nextInt(ch-10)+5, RandomGray()));
        delete map;
        map=new Image(Image::ARGB,cw,ch,true);

        UpdateMap();
    }

    void timerCallback()
    {
        hexcursoropacity+=hexcursoropacityinc;
        if ((hexcursoropacity > 0.9f) || (hexcursoropacity < 0.1f) ) hexcursoropacityinc*=-1;
		//UpdateMap();repaint();
    }

    void paint (Graphics& g)
    {
        g.drawImageAt(map,0,0,false);
    }

    void UpdateMap(int mousex= -1,int mousey= -1)
    {
        Graphics g (*map);

        g.fillAll(theme->bg);
		g.setColour(theme->border);
        g.fillRoundedRectangle(0,0,(float)cw,(float)ch,7);
		g.setColour(theme->borderhigh);
		g.fillRoundedRectangle(1,1,(float)(cw-2),(float)(ch-2),7);
        g.setColour(theme->border);
        g.fillRoundedRectangle(2,2,(float)(cw-4),(float)(ch-4),7);
        g.setColour(theme->mapbg);
        g.fillRoundedRectangle(3,3,(float)(cw-6),(float)(ch-6),7);

        //Stars.remove(0);
        //Stars.add(new Star(juce::Random::getSystemRandom().nextInt(cw-10)+5, juce::Random::getSystemRandom().nextInt(ch-10)+5, RandomGray()));
        //for (int w=0; w < starmax; w++)
        //{
        //    g.setColour(Stars[w]->c);
        //    g.setPixel(Stars[w]->x,Stars[w]->y);
        //}

        for (int u=0; u<20; u++)
            for (int v=0; v<20; v++)
                DrawAHex(g,*heximg, u,v);
				
        //territory
        for (int u=0;u<63;u++)
            for (int v=0;v<63;v++)
            {
                int c=map_terr[u][v];
                if (c==99) DrawAHex(g,*hexdisputed,u,v);
                if ((c>0) && (c<=4))
                {
                    DrawAHex(g,*Stain[(c-1)*7],u,v);
                    for (int i=1;i<=6;i++)
                    {
                        Coords j=HexDirection(Coords(u,v),i);
                        if (map_terr[j.x][j.y] != c)
                            DrawAHex(g,*Stain[((c-1)*7)+i],u,v);
                    }
                }
            }

        for (int i=0;i<Entities.size();i++)
        {
            if (Entities[i]->img!=0)
                DrawAPiece(g,*Entities[i]->img,Entities[i]->coords.x,Entities[i]->coords.y);

            if (Entities[i]->etype==Entity::nebula)
                DrawAPiece(g,*nebulaimage,Entities[i]->coords.x,Entities[i]->coords.y);

            g.setColour(theme->maptext);
            g.setFont(*font);
            if (Entities[i]->etype==Entity::planet)
            {
                //int w=font->getStringWidth(Entities[i]->name)/2;
                //int ex=Entities[i]->coords.x;
                //int ey=Entities[i]->coords.y;
                //int hhx=HexXtoXcenter(ex,ey);
                //int hhy=HexYtoYcenter(ey);
                //g.drawSingleLineText(Entities[i]->name,hhx-w+5,hhy+(int)hexs+5);
                DrawText(g,*Entities[i]);
            }
        }

        if ( (mousex >=0) && (mousey >= 0))
        {
            Coords m=XYtoHexXY((float)mousex,(float)mousey);
            g.setOpacity(hexcursoropacity);
            DrawAHex(g,*hexcursor,m.x,m.y);
            //g.setColour(Colours::plum);
            //g.fillPath(hexcursor);
        }

        if ((anchor.x >=0) && (anchor.y>=0))
        {
            Coords m=XYtoHexXY((float)anchor.x,(float)anchor.y);
            g.setOpacity(1.0f);
            DrawAHex(g,*hexanchor,m.x,m.y);
            if ((dest.x >=0) && (dest.y>=0))
            {
                FindPath(anchor,dest);
                for (int i=0;i<PathStep.size();i++)
                {
                    DrawAHex(g,(i<pmovemax)?*hexpath:*hexpathred,PathStep[i].x,PathStep[i].y);
                }
            }
        }

        for (int i=0;i<=cMoveIndex;i++)
        {
            Path p;
            p.clear();
            p.startNewSubPath(HexXtoXcenter(packx(cMoveStep[i][1]),packy(cMoveStep[i][1])),HexYtoYcenter(packy(cMoveStep[i][1])));
            int j;
            for (j=2;j<=cMoveStep[i][0];j++)
                p.lineTo(HexXtoXcenter(packx(cMoveStep[i][j]),packy(cMoveStep[i][j])),HexYtoYcenter(packy(cMoveStep[i][j])));
                /*p.cubicTo(
                HexXtoXcenter(packx(cMoveStep[i][j-1]),packy(cMoveStep[i][j-1])),HexYtoYcenter(packy(cMoveStep[i][1])),
                HexXtoXcenter(packx(cMoveStep[i][j+1]),packy(cMoveStep[i][j+1])),HexYtoYcenter(packy(cMoveStep[i][j+1])),
                HexXtoXcenter(packx(cMoveStep[i][j]),packy(cMoveStep[i][j])),HexYtoYcenter(packy(cMoveStep[i][j]))
                );
            p.lineTo(HexXtoXcenter(packx(cMoveStep[i][j+1]),packy(cMoveStep[i][j+1])),HexYtoYcenter(packy(cMoveStep[i][j+1])));
            */
            g.setColour(theme->hexpathcolor);
            g.setOpacity(0.8f);
            g.strokePath(p,PathStrokeType(3.0f));
            j--;
            g.fillEllipse(HexXtoXcenter(packx(cMoveStep[i][j]),packy(cMoveStep[i][j])),HexYtoYcenter(packy(cMoveStep[i][j])),hexa/3.0f,hexb/3.0f);

                //DrawAPiece(g,*hexmovestep,packx(cMoveStep[i][j]),packy(cMoveStep[i][j]));
        }
    }

    int HexXtoXcenter(int u, int v) { return (int) (( (float)u * hexa + (float)(v % 2) * hexr) + hexa / 2.0f); }

    int HexYtoYcenter(int v) { return (int) ( (float)v * (hexh+hexs) + (hexh+hexs) / 2.0f); }

    Coords XYtoHexXY(int msx, int msy)
    {
        return XYtoHexXY((float)msx,(float)msy);
    }

    Coords XYtoHexXY(float msx, float msy)
    {
        //if (msx==62.0f)
        //    msx=62.0f;
        //if (msx==63.0f)
        //    msx=63.0f;
        float xsect = msx / hexa;
        float ysect = msy / (hexh+hexs);
        float xsectpxl = (float)( (int)msx % (int)hexa );
        float ysectpxl = (float)( (int)msy % (int)(hexh+hexs) );
        float hx,hy;

        if ( ((int)ysect % 2) == 0)
        { // A-section
            hy=ysect;
            hx=xsect;
            if ( ysectpxl < ( hexh - xsectpxl * hexm) )
            {
                hy=ysect-1.0f;
                hx=xsect-1.0f;
            }
            if ( ysectpxl < (-hexh + xsectpxl * hexm) )
            {
                hy=ysect-1.0f;
                hx=xsect;
            }
        }
        else
        { // B-section
            if ( (xsectpxl>=hexr) || (xsectpxl == 0.0f) )
            {
                if ( ysectpxl < ((2.0f*hexh) - (xsectpxl*hexm)) )
                {
                    hy=ysect-1.0f;
                    hx=xsect;
                }
                else
                {
                    hy=ysect;
                    hx=xsect;
                }
            }
            if ( (xsectpxl<hexr) && (xsectpxl != 0.0f) )
            {
                if (ysectpxl < (xsectpxl*hexm) )
                {
                    hy=ysect-1.0f;
                    hx=xsect;
                }
                else
                {
                    hy=ysect;
                    hx=xsect-1.0f;
                }
            }
        }
        return Coords((int)hx,(int)hy);
    }

    void setHexers(float s)
    {
        hexs = s;
        hexh=hexs * 0.5f; //sin30
        hexr=hexs * 0.866025404f; //cos30
        hexb=hexs+2.0f*hexh;
        hexa=2.0f*hexr;
        hexm=hexh / hexr;
    }
    
    void ReDoGFX(float r)
    {
       setHexers(r);

       delete heximg;
       heximg=new Image(Image::ARGB,(int)hexa+2,(int)hexb+2,true);
       Graphics g (*heximg);
       //g.setColour(Colours::black);
       //g.fillAll();

       hp.clear();
       hp.startNewSubPath(hexr,0.0f);
       hp.lineTo(hexa,hexh);
       hp.lineTo(hexa,hexh+hexs);
       hp.lineTo(hexr,hexb);
       hp.lineTo(0.0f,hexh+hexs);
       hp.lineTo(0.0f,hexh);
       hp.closeSubPath();

       //hexcursor.clear();
       //hexcursor=heximg;

       g.setColour(theme->grid);
       g.setOrigin(0,0);
       g.strokePath(hp,PathStrokeType(0.7f));

       delete hexcursor;
       hexcursor=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
       Graphics g2 (*hexcursor);
       g2.setColour(theme->hexcursorcolor);
       g2.setOpacity(1.0f);
       g2.fillPath(hp);

       delete hexdisputed;
       hexdisputed=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
       Graphics g4(*hexdisputed);
       g4.setColour(theme->hexdisputedcolor);
       g4.setOpacity(0.5f);
       Coords zero(0,0);
       for (int dy=0;dy<(int)hexb;dy++)
           for (int dx=dy%6;dx<(int)hexa;dx+=6)
               if (XYtoHexXY(dx,dy)==zero)
                   g4.setPixel(dx,dy);

       Stain.clear();
       Image *itmp;
       float w=2.0f;
       for (int i=1;i<=4;i++)
           for (int j=0;j<=6;j++)
           {
               itmp=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
               Graphics gi(*itmp);
               if (j==0)
               {
                   gi.setColour((*StainColor[i]).darker());gi.setOpacity(0.3f); gi.fillPath(hp);
               }
               else
               {
                   gi.setColour(*StainColor[i]);
                   gi.setOpacity(1.0f);
                   if (j==1) gi.drawLine(hexr,     0.0f,     hexa-1.0f,hexh,     w);
                   if (j==2) gi.drawLine(hexa-1.0f,hexh,     hexa-1.0f,hexh+hexs,w);
                   if (j==3) gi.drawLine(hexa-1.0f,hexh+hexs,hexr,     hexb-1.0f,w);
                   if (j==4) gi.drawLine(hexr,     hexb-1.0f,0.0f,     hexh+hexs,w);
                   if (j==5) gi.drawLine(0.0f,     hexh+hexs,0.0f,     hexh,     w);
                   if (j==6) gi.drawLine(0.0f,     hexh,     hexr,     0.0f,     w);
               }
               Stain.add(itmp);
           }


       delete nebulaimage;
       nebulaimage=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
       Graphics g3 (*nebulaimage);
       g3.setOpacity(0.5f);
       int p=((int)hexa)/4+1;
       int q=((int)hexb)/4+1;
       for (int i=0;i<250;i++)
       {
           g3.setColour(Colour(130+Rand(-50,50),0,160+Rand(-50,50)));
           g3.setPixel(
               Rand(0,p)+Rand(0,p)+Rand(0,p)+Rand(0,p),
               Rand(0,q)+Rand(0,q)+Rand(0,q)+Rand(0,q)
               );
       }

       delete hexanchor;
       hexanchor=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
       Graphics g5(*hexanchor);
       g5.setColour(theme->hexanchorcolor);
       g5.strokePath(hp,PathStrokeType(1.0f));

       delete hexpath;
       hexpath=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
       Graphics g6(*hexpath);
       g6.setColour(theme->hexpathcolor);
       g6.fillPath(hp);

       delete hexpathred;
       hexpathred=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
       Graphics g7(*hexpathred);
       g7.setColour(theme->hexpathredcolor);
       g7.fillPath(hp);

       delete hexmovestep;
       hexmovestep=new Image(Image::ARGB,(int)hexa+1,(int)hexb+1,true);
       Graphics g8(*hexmovestep);
       g8.setColour(theme->hexpathcolor);
       g8.fillEllipse(hexa/2.0f, hexb/2.0f, hexa/4.0f, hexb/4.0f);
    }


    void DrawText(Graphics& g, const Entity& e, bool ignorevis=VIS)
    {
        int w=font->getStringWidth(e.name)/2;
        int ex=e.coords.x;
        int ey=e.coords.y;
        int hxx=HexXtoXcenter(ex,ey);
        int hyy=HexYtoYcenter(ey);
        if ( (ignorevis) ||
             (map_vis[ex][ey] > 0) )
            g.drawSingleLineText(e.name,hxx-w+5,hyy+(int)hexs+5);
    }
    
    void DrawAHex(Graphics& g, const Image& i, int hxx, int hyy, bool ignorevis=VIS)
    {
        int xpx,ypx;
        xpx= (int)((float)hxx * hexa + (float)(hyy % 2) * hexr);
        ypx= (int)((float)hyy * (hexh + hexs));
        if ( (ignorevis) ||
             (map_vis[hxx][hyy] > 0) )
            g.drawImageAt(&i,xpx+4,ypx+4);
    }

    void DrawAPiece(Graphics& g, const Image& i, int hxx, int hyy, bool ignorevis=false)
    {
        int xpx,ypx;
        xpx= (int)((float)hxx * hexa + (float)(hyy % 2) * hexr);
        ypx= (int)((float)hyy * (hexh + hexs));
		xpx=xpx+(int)(hexa/2.0f);
		ypx=ypx+(int)(hexb/2.0f);
		xpx=xpx-i.getWidth()/2;
		ypx=ypx-i.getHeight()/2;
        if ( (ignorevis) ||
             (map_vis[hxx][hyy] > 0) )
            g.drawImageAt(&i,xpx+5,ypx+5);
    }

    void SetParent(int n, int p) { NodeParent[n]=p; }
    void SetG(int n, int c) { NodeG[n]=c; }
    void SetH(int n, int h) { NodeH[n]=h; }
    void SetOpen(int n) { ClosedList.removeValue(n); OpenList.addIfNotAlreadyThere(n); }
    void SetClosed(int n) { OpenList.removeValue(n); ClosedList.addIfNotAlreadyThere(n); }
    int GetBestNode()
    {
        int best=9999;
        int j=-1;
        for (int i=0;i<OpenList.size();i++)
        {
            int tmp=OpenList[i];
            int g=NodeG[tmp];
            int h=NodeH[tmp];
            int sc=NodeG[OpenList[i]]+NodeH[OpenList[i]];
            if (sc<best) { best=sc; j=OpenList[i]; }
        }
        return j;
    }

    void FindPath(Coords a, Coords b) //takes mouse xy and converts to hex before find path
    {
        Coords c=XYtoHexXY((float)a.x,(float)a.y);
        Coords d=XYtoHexXY((float)b.x,(float)b.y);
        FindPath(c.x,c.y,d.x,d.y);
    }

    void FindPath(int ahx, int ahy, int mhx, int mhy)
    {
        for (int i=0;i<PACKBASEQ;i++)
        {
            NodeG[i]=999; NodeH[i]=999; NodeParent[i]=-1;
        }
        int goal=pack(mhx,mhy);
        int start=pack(ahx,ahy);
        OpenList.clear();
        ClosedList.clear();
        OpenList.add(start);
        SetG(start,10);
        SetH(start,HexDistGuess(start,goal)*10);
        int finished=0;
        while (finished==0) //0 still working, 1 found, 2 impossible
        {
            // add something here to see if it's taking too long
            int current=GetBestNode();
            if (current==-1)
            {
                finished=2;
            }
            else
            {
                SetClosed(current);
                if (current==goal)
                {
                    finished=1;
                }
                else
                {
                    DoPath(current+( 0*PACKBASE+(-1)),current,goal);
                    DoPath(current+( 1*PACKBASE+( 0)),current,goal);
                    DoPath(current+( 0*PACKBASE+( 1)),current,goal);
                    DoPath(current+(-1*PACKBASE+( 0)),current,goal);
                    if ((packy(current) % 2) == 0)
                    {
                        DoPath(current+(-1*PACKBASE+(-1)),current,goal);
                        DoPath(current+(-1*PACKBASE+( 1)),current,goal);
                    }
                    else
                    {
                        DoPath(current+( 1*PACKBASE+(-1)),current,goal);
                        DoPath(current+( 1*PACKBASE+( 1)),current,goal);
                    }
                }
            }
        }
        if (finished==2)
        {
            AlertWindow::showMessageBox(AlertWindow::InfoIcon,T("Pathfinding error"),T("start=")+String(start)+T(" goal=")+String(goal),T("OK"));
        }
        else
        {
            int t=goal;
            PathStep.clear();
            while (t!=start)
            {
                PathStep.insert(0,Coords(packx(t),packy(t)));
                t=NodeParent[t];
            }
            PathStep.insert(0,Coords(packx(t),packy(t)));
        }
    }


    void DoPath(int successor, int current, int goal)
    {
        if (successor<0) return;
        if (successor>PACKBASEQ) return;
        if ((successor % PACKBASE) > (PACKBASE * 0.9)) return;
        int sx=packx(successor);
        int sy=packy(successor);
        if ((sx<0) || (sx>PACKBASE)) return;
        if ((sy<0) || (sy>PACKBASE)) return;

        if (!ClosedList.contains(successor))
        {
            if (!OpenList.contains(successor))
            {
                SetOpen(successor);
                SetParent(successor,current);
                SetG(successor,NodeG[current]+map_cost[sx][sy]);
                SetH(successor,HexDistGuess(successor,goal)*10);
            }
            else
            {
                if ((NodeG[current]+map_cost[sx][sy]) < NodeG[successor])
                {
                    SetParent(successor,current);
                    SetG(successor,NodeG[current]+map_cost[sx][sy]);
                }
            }
        }
    }

   
    int pnum;
    int pmovemax;
    int map_vis[64][64];
    int map_terr[64][64];
    int map_cost[64][64];
    OwnedArray<Entity> Entities;
    OwnedArray<Image> ShipImage;
    OwnedArray<Image> PlanetImage;
    OwnedArray<Colour> StainColor;
    OwnedArray<Image> Stain;
    Coords anchor;
    Coords dest;
    int NodeParent[PACKBASEQ];
    int NodeG[PACKBASEQ];
    int NodeH[PACKBASEQ];
    Array<Coords> PathStep;
    Array<int> OpenList;
    Array<int> ClosedList;
    StringArray cMoveName;
    int cMoveStep[20][20];
    int cMoveIndex;
    String cMover;



// ----------------------
private:
    Theme *theme;
    Image *map;
    OwnedArray<Star> Stars;
    float hexs,hexh,hexr,hexb,hexa;
    float hexm;
    Image *heximg;
    int cw,ch;
    int starmax;
    Colour bgcolor;
    Path hp;
    Image *hexcursor;
    float hexcursoropacity, hexcursoropacityinc;
    Font *font;
    Image *nebulaimage;
    Image *hexdisputed;
    Image *hexanchor;
    Image *hexpath;
    Image *hexpathred;
    Image *hexmovestep;

};




//==============================================================================
/** This is the component that sits inside the "hello world" window, filling its
    content area. In this example, we'll just write "hello world" inside it.
*/
class HelloWorldContentComponent    : public Component, public ButtonListener , public SliderListener
{
public:
    HelloWorldContentComponent(Theme *t)
    {
		theme=t;
        tt=new TooltipWindow();
        gamemode=false; configok=false;

        infobox=new Label(T("infobox"),T(""));
        infobox->setBounds(2,30,290,150);
		infobox->setColour(Label::backgroundColourId,Colours::white);
		infobox->setColour(Label::outlineColourId,Colours::white.darker());
        infob(T("+++"));
        infob(T("ATZ"));
        infob(T("ATS11=30 DT9,1800HEXIBEX"));
        infob(T("CONNECT 1200"));
        infob(T("Welcome to Ibex..."));
        addAndMakeVisible(infobox);

        mapcom=new MapComponent(theme);
        bool m=LoadMedia();
        if (m)
        {
            if (ParseConfig())
            {
                configok=ParseMap();
            }
        }

        if (configok)
        {
            bc=new TextButton(T("Commence"));
            bc->setBounds(5,5,100,20);
            bc->addButtonListener(this);
            bc->setColour(TextButton::buttonColourId,theme->button);
            addAndMakeVisible(bc);
            infob(T("Please press the \"Commence\" button to play."));
        }
        else
        {
            bf=new TextButton(T("Exit"));
            bf->setBounds(5,5,100,20);
            bf->addButtonListener(this);
            bf->setColour(TextButton::buttonColourId,theme->button);
            addAndMakeVisible(bf);
            infob(T("LOST CARRIER~.}}"));
            infob(T("Press the \"Exit\" button."));
        }

        bgcolor=theme->bg;
    }

    ~HelloWorldContentComponent()
    {
        deleteAllChildren();
        delete tt;
    }

    void paint (Graphics& g)
    {
        // clear the background with solid white
        g.fillAll (theme->bg);

        // set our drawing colour to black..
        //g.setColour (Colours::indigo);

        // choose a suitably sized font
        //g.setFont (20.0f, Font::bold);

        // and draw the text, centred in this component
        //g.drawText (T("Hello World!"), 0, 0, getWidth(), getHeight()-20, Justification::centred, false);

    }

    void buttonClicked(Button* b)
    {
        switch (gamemode)
        {    
        case true:
            if (b == b1)
                //JUCEApplication::quit();
                l1->setText(Time::getCurrentTime().toString(true,true),false);
                //l1->setText(Utils::net(),false);
                //l1->setText(String(mapcom->getX()),true);
            break;
        case false:
            if (b == bc) switchToGameMode();
            if (b == bf) JUCEApplication::quit();
        }
    }

    void mouseMove(const MouseEvent& e)
    {
        if (e.eventComponent==mapcom)
        {
            //l1->setText(String(e.x)+T(",")+String(e.y),true);
            //l1->setText(String(e.x)+T(",")+String(e.y),true);
            //l1->setText(Time::getCurrentTime().toString(true,true),false);

            mapcom->dest=Coords(e.x,e.y);
            mapcom->UpdateMap(e.x,e.y);
			Coords c=mapcom->XYtoHexXY((float)e.x,(float)e.y);
			//l1->setText(T("[")+String(c.x)+T(",")+String(c.y)+T("]"),false);
            
            String desc=String::empty;
            Entity *en=getEntity(c.x,c.y);
            if (en!=0) desc=en->name+T(",")+Players[T("race")+String(en->owner)];
            info->setD(
                T("[")+String(c.x)+T(",")+String(c.y)+T("]\n")+
                desc                
                );
            mapcom->repaint();
        }
    }

    void mouseExit(const MouseEvent& e)
    {
        if (e.eventComponent==mapcom)
        {
            mapcom->anchor=Coords(-1,-1);
            mapcom->dest=Coords(-1,-1);
            mapcom->cMover=String::empty;
            mapcom->UpdateMap();
            mapcom->repaint();
        }
    }

    void mouseDown(const MouseEvent& e)
    {
        if (e.eventComponent==mapcom)
        {
            if (mapcom->anchor==Coords(-1,-1))
            {
                Coords c=mapcom->XYtoHexXY((float)e.x,(float)e.y);
                Entity *en=getEntity(c.x,c.y);
                if (en!=0)
                    if (en->etype==Entity::player)
                        if (en->owner==pnum)
                        {
                            mapcom->anchor=Coords(e.x,e.y);
                            mapcom->cMover=en->name;
                        }
            }
            else
            {
                mapcom->cMoveIndex++;
                int i;
                for (i=0;i<mapcom->PathStep.size();i++)
                {
                    mapcom->cMoveStep[mapcom->cMoveIndex][i+1]=pack(mapcom->PathStep[i].x,mapcom->PathStep[i].y);
                }
                mapcom->cMoveStep[mapcom->cMoveIndex][0]=i;
                mapcom->cMoveName.insert(mapcom->cMoveIndex,String(T("Move #")+String(mapcom->cMoveIndex+1)+T(" ")+mapcom->cMover));
                mq->add(mapcom->cMoveName[mapcom->cMoveIndex]);
                mapcom->anchor=Coords(-1,-1);
                mapcom->dest=Coords(-1,-1);
                mapcom->cMover=String::empty;
                mapcom->UpdateMap();
                mapcom->repaint();
            }
        }
    }

    void mouseUp(const MouseEvent& e)
    {
        if (e.eventComponent==mapcom)
            l1->setText(String(e.x)+T(",")+String(e.y),true);
    }
	
	void sliderValueChanged(Slider* s)
	{
		l1->setText(String(s->getValue()),true);
		mapcom->ReDoGFX((float)s->getValue());
		mapcom->UpdateMap();
		mapcom->repaint();
	}
	void sliderDragStarted(Slider* s){}
	void sliderDragEnded(Slider* s){}
	
    void resized()
    {
        if (gamemode)
        {
            int cw=this->getWidth();
            int ch=this->getHeight();
            ca.animateComponent(mapcom,Rectangle(20,95,(cw*3)/4-40,ch-120),1000,0.0,0.0);
            ca.animateComponent(info,Rectangle( (cw*3)/4,95,cw/4-20,ch-120),1500,1.0,0.0);
            //mapcom->setBounds(20,95,this->getWidth()-40,this->getHeight()-120);
        }
    }

    void infob(const String& a)
    {
        infobox->setText(infobox->getText() + T("\n") + a,false);
    }

    void switchToGameMode()
    {
        deleteAllChildren();

        AlertWindow::showMessageBox(AlertWindow::InfoIcon,T("Message of the day..."),motd,T("OK"));

        b1=new TextButton(T("Info"));
        b1->setBounds(5,5,100,20);
        b1->setTooltip(T("Please do not press this button"));
        b1->addButtonListener(this);
        b1->setColour(TextButton::buttonColourId,theme->button);
        b1->setColour(TextButton::textColourId,Colours::black);
        //b1->setConnectedEdges(Button::ConnectedOnBottom|Button::ConnectedOnTop);
        addAndMakeVisible(b1);

        l1=new Label(T("Label1"),Utils::Now());
        l1->setBounds(5,25,200,60);
		l1->setColour(Label::textColourId,theme->text);
        addAndMakeVisible(l1);

        //mapcom=new MapComponent(theme);
        //mapcom->setBounds(20,95, (this->getWidth()*3)/4-40 ,this->getHeight()-120);
		mapcom->setBounds(getWidth()*3/8,getHeight()/2,1,1);
        mapcom->addMouseListener(this,true);
        addAndMakeVisible(mapcom);
		ca.animateComponent(mapcom,Rectangle(20,95, (this->getWidth()*3)/4-40 ,this->getHeight()-120),3000,0.0,0.0);

        //!/bgcolor=Colour(240,240,200);
		bgcolor=theme->bg;

		hexslider=new Slider(T("hexslider"));
		hexslider->setSliderStyle(Slider::LinearHorizontal);
		hexslider->setBounds(110,25,100,25);
		hexslider->setRange(5,50,1);
		hexslider->addListener(this);
		hexslider->setTextBoxStyle(Slider::NoTextBox,true,0,0);
		hexslider->setColour(Slider::thumbColourId,theme->button);
		hexslider->setColour(Slider::trackColourId,theme->button.darker());
		hexslider->setColour(Slider::backgroundColourId,theme->bg);
		hexslider->setValue(18.0,false,false);
		addAndMakeVisible(hexslider);

        info=new InfoComponent(theme);
        info->setBounds(getWidth()*7/8,getHeight()/2,1,1);
        info->setTopText(T("Analysis"));
        addAndMakeVisible(info);
        int cw=this->getWidth();
        int ch=this->getHeight();
        ca.animateComponent(info,Rectangle( (cw*3)/4+10,95,cw/4-20,ch-120),2000,0.0,0.0);

        mq=new MovementQueueComponent(theme);
        mq->setBounds(10,10,1,1);
        mq->setTopText(T("Movement Queue"));
        //for (int i=1;i<10;i++) mq->add(T("This "+String(i)));
        addAndMakeVisible(mq);
        ca.animateComponent(mq,Rectangle(50,50,200,100),1000,0.0,0.0);

        gamemode=true;
        repaint();
        
    }

    const String ServerGet(const String& cmd)
    {
        String uri;
        //uri.printf(T("%s/%i/%s"),server.toUTF8(),pnum,cmd.toUTF8());
        uri=server+T("/")+String(pnum)+T("/")+cmd;
        URL u(uri);
        return (u.readEntireTextStream());
    }

    bool LoadMedia()
    {
        File media;
        String filename("ibex.media");
        media=AppDir() + filename;
        if (!media.existsAsFile())
        {
            media=ExeDir() + filename;
            if (!media.existsAsFile())
            {
                infob(T("Unable to find media assets."));
                return false;
            }
        }
        return (LoadSounds(media) && LoadImages(media));
    }

    bool LoadSounds(const File& media)
    {
        return true;
    }

    bool LoadImages(const File& media)
    {
        mapcom->ShipImage.add(new Image(Image::ARGB,1,1,false)); //dummy to make 1-based

        ZipFile *z;
        z=new ZipFile(media);

        Image *ii;
        InputStream *is;

        //ii=ImageFileFormat::loadFrom(File(AppDir()+T("media/eyeguy1a.png")));

        is=z->createStreamForEntry(z->getIndexOfFileName(T("eyeguy1a.png")));
        ii=ImageFileFormat::loadFrom(*is);
        mapcom->ShipImage.add(new Image(*ii));
        delete ii;
        delete is;
        is=z->createStreamForEntry(z->getIndexOfFileName(T("snakeguy1a.png")));
        ii=ImageFileFormat::loadFrom(*is);
        mapcom->ShipImage.add(new Image(*ii));
        delete ii;
        delete is;
        is=z->createStreamForEntry(z->getIndexOfFileName(T("mageguy1a.png")));
        ii=ImageFileFormat::loadFrom(*is);
        mapcom->ShipImage.add(new Image(*ii));
        delete ii;
        delete is;
        is=z->createStreamForEntry(z->getIndexOfFileName(T("purpledragonguy1a.png")));
        ii=ImageFileFormat::loadFrom(*is);
        mapcom->ShipImage.add(new Image(*ii));
        delete ii;
        delete is;

        is=z->createStreamForEntry(z->getIndexOfFileName(T("jupiter.png")));
        ii=ImageFileFormat::loadFrom(*is);
        mapcom->PlanetImage.add(new Image(*ii));
        delete ii;
        delete is;

        delete z;
        return true;
    }

    bool ParseConfig()
    {
        pnum=0; server=T("");

        File configfile (AppDir() + T("/ibexconfig"));
        if (!configfile.existsAsFile())
        {
            infob(T("Unable to find ibexconfig file."));
        }
        else
        {
            FileInputStream ip (configfile);
            String s;

            s=ip.readNextLine();
            if (s!=T("[Ibex]"))
            {
                infob(T("Malformed config file."));
            }
            else
            {
                while (!ip.isExhausted())
                {
                    s=ip.readNextLine();
                    int p=s.indexOfChar('=');
                    if (p>=0)
                    {
                        String l=s.substring(0,p);
                        String r=s.substring(p+1);
                        if (l==T("server")) server=r;
                        if (l==T("player")) {pnum=r.getIntValue();mapcom->pnum=pnum;}
                    }
                }
                if ( (server.length()==0) || (pnum==0) )
                {
                    infob(T("error parsing config file"));
                }
                else
                {
                    //infob(T("server: ")+server);
                    //infob(T("player: ")+String(pnum));
                    infob(T("Pinging ")+server);
                    if (FILEGET)
                        resp=T("ACKK");
                    else
                        resp=ServerGet(T("PING"));

                    infob(resp);

                    if (!resp.startsWith(T("ACK")))
                    {
                        infob(T("Unexpected server response."));
                    }
                    else
                    {
                        infob(T("Getting turn"));
                        if (FILEGET)
                        {
                            File fq (AppDir() + T("/getturn.txt"));
                            resp=fq.loadFileAsString();
                            //resp.printf(T("[Ibex]%c%cdog=red%c%ccat=muse%c%c"),13,10,13,10,13,10);
                        }
                        else
                            resp=ServerGet(T("GETTURN"));

                        bool turnok=initTurn();

                        if (!turnok)
                        {
                            infob(T("some sort of error parsing turn file"));
                        }
                        else
                        {
                            infob(T("Getting map"));
                            if (FILEGET)
                            {
                                File fq (AppDir() + T("/getmap.txt"));
                                resp=fq.loadFileAsString();
                            }
                            else
                                resp=ServerGet(T("GETMAP"));

                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    bool ParseMap()
    {
        StringArray map;
        map.addLines(resp);
        if (map[0]!=T("[IbexMap]"))
        {
            infob(T("Bad map"));
            return false;
        }

        for (int i=0;i<64;i++)
            for (int j=0;j<64;j++)
                mapcom->map_terr[i][j]=0;

        int /*p1,*/p2,p3,p4,p5,p6,p7,map_pnum;
        String map_e,map_n,map_ex,map_ey,map_o,map_p1,map_p2;
        for (int i=1;i<=map.size();i++)
        {
            if (map[i].length()>0)
            {
                p2=map[i].indexOfChar(':');
                p3=map[i].indexOfChar('~');
                p4=map[i].indexOfChar(p3,',');
                p7=map[i].indexOfChar('!');
                if (p7==-1) p7=map[i].length();
                map_e=map[i].substring(0,p2);
                map_n=map[i].substring(p2+1,p3);
                map_ex=map[i].substring(p3+1,p4);
                map_ey=map[i].substring(p4+1,p7);
                map_o=map[i].substring(p7+1);
                if (map_e==T("planet"))
                {
                    p5=map_n.indexOfChar('@');
                    p6=map_n.indexOfChar(p5+1,'@');
                    map_p1=map_n.substring(p5+1,p6);
                    map_p2=map_n.substring(p6+1);
                    map_n=map_n.substring(0,p5);
                }
                map_pnum=0;
                if (map_e.startsWith(T("player")))
                    map_pnum=map_e.substring(6).getIntValue();

                if (map_e==T("planet"))
                {
                    mapcom->Entities.add(new Entity(
                        Entity::planet,
                        map_o.getIntValue(),
                        mapcom->PlanetImage[0],
                        Coords(map_ex.getIntValue(),map_ey.getIntValue()),
                        map_n,
                        map_p1,
                        map_p2.getIntValue(),
                        1
                        ));
                }
                if (map_e==T("nebula"))
                {
                    mapcom->Entities.add(new Entity(
                        Entity::nebula,
                        0,
                        0,
                        Coords(map_ex.getIntValue(),map_ey.getIntValue()),
                        map_n,
                        T("Nebula"),
                        0,
                        50
                        ));
                }
                if (map_e.startsWith(T("player")))
                {
                    Coords c(map_ex.getIntValue(),map_ey.getIntValue());
                    mapcom->Entities.add(new Entity(
                        Entity::player,
                        map_pnum,
                        mapcom->ShipImage[map_pnum],
                        c,
                        map_n,
                        String::empty,
                        0,
                        50
                        ));
                    for (int i=1;i<=6;i++)
                    {
                        Coords t(HexDirection(c,i));
                        if (CoordsInBounds(t))
                        {
                            if ( (mapcom->map_terr[t.x][t.y]==0) || (mapcom->map_terr[t.x][t.y]==map_pnum) )
                                mapcom->map_terr[t.x][t.y]=map_pnum;
                            else
                                mapcom->map_terr[t.x][t.y]=99; //disputed
                        }
                    }
                }
            }
        }
        for (int i=0;i<mapcom->Entities.size();i++)
        {
            Entity *e=mapcom->Entities[i];
            mapcom->map_terr[e->coords.x][e->coords.y]=e->owner;
        }
        //from LoadMapCosts fn
        for (int i=0;i<64;i++)
            for (int j=0;j<64;j++)
            {
                int k=10;
                if (mapcom->map_terr[i][j]!=pnum) k+=20;
                else k-=5;
                mapcom->map_cost[i][j]=k;
            }
        for (int i=0;i<mapcom->Entities.size();i++)
        {
            Entity *e=mapcom->Entities[i];
            mapcom->map_cost[e->coords.x][e->coords.y]+=e->movecost;
        }
        mapcom->map_terr[4][1]=99;

        return true;
    }

    bool initTurn()
    {
        pmovemax=ParseTurn(resp,T("moves")).getIntValue();mapcom->pmovemax=pmovemax;
        motd=ParseTurn(resp,T("motd"));
        title=ParseTurn(resp,T("title"));
        dynasty=ParseTurn(resp,T("dynasty"));
        firstname=ParseTurn(resp,T("firstname"));
        turnnum=ParseTurn(resp,T("turn")).getIntValue();
        String key;
        for (int i=1;i<=4;i++)
        {
            key=T("race")+String(i);     Players.set(key,ParseTurn(resp,key));
            key=T("dynasty")+String(i);  Players.set(key,ParseTurn(resp,key));
            key=T("firstname")+String(i);Players.set(key,ParseTurn(resp,key));
            key=T("title")+String(i);    Players.set(key,ParseTurn(resp,key));
        }
        String vis=ParseTurn(resp,T("vismap"));
        for (int i=0;i<=63;i++)
            for (int j=0;j<=63;j++)
                mapcom->map_vis[i][j]=0;
        if (vis.length()>0)
        {
            for (int i=0;i<vis.length();i++)
            {
                int j=i / 64;
                int k=i % 64;
                mapcom->map_vis[j][k]=vis[i]-'0';
            }
        }
        prodmax=0;
        ProdEvents.add(new ProdEvent(0,String::empty,String::empty,0)); //dummy one so OwnedArray index == production item index
        for (int i=1;i<=20;i++)
        {
            key=ParseTurn(resp,T("production"+String(i)));
            if (key.length()>0)
            {
                prodmax=i;
                StringArray sa;
                sa.addTokens(key,T(":"),String::empty);
                ProdEvents.add(new ProdEvent(i,sa[0],sa[1],sa[2].getIntValue()));
            }
        }
        key=T("natrium");   Resource.set(key,ParseTurn(resp,key));
        key=T("dysprosium");Resource.set(key,ParseTurn(resp,key));
        key=T("pentium");   Resource.set(key,ParseTurn(resp,key));
        key=T("smithore");  Resource.set(key,ParseTurn(resp,key));
        key=T("money");     Resource.set(key,ParseTurn(resp,key));
        bgmfn=ParseTurn(resp,T("bgm"));
        return true;
    }

    
    Entity* getEntity(int x, int y)
    {
        for (int i=0;i<mapcom->Entities.size();i++)
        {
            Entity *e=mapcom->Entities[i];
            if ((e->coords.x==x) && (e->coords.y==y))
                if (mapcom->map_vis[x][y]>0)
                    return e;
        }
        return 0;
    }


private:
    TextButton *b1, *bc, *bf;
    TooltipWindow *tt;
    Label *l1;
    MapComponent *mapcom;
	Slider *hexslider;
    Label *infobox;
    ComponentAnimator ca;
    InfoComponent* info;
    MovementQueueComponent* mq;

    Coords pos;
    Colour bgcolor;

    bool gamemode, configok;
    String server,resp;
    int pnum;
    int pmovemax;
    String motd;
    String title, dynasty, firstname;
    int turnnum;
    StringPairArray Players;
    OwnedArray<ProdEvent> ProdEvents;
    int prodmax;
    StringPairArray Resource;
    String bgmfn;
	Theme *theme;
};



//==============================================================================
/** This is the top-level window that we'll pop up. Inside it, we'll create and
    show a HelloWorldContentComponent component.
*/
class HelloWorldWindow  : public DocumentWindow
{
public:

    Theme theme;

    //==============================================================================
    HelloWorldWindow()
        : DocumentWindow (
                          String(T("Ibex ")) + T(IBEXVER),
                          Colours::lightgrey, 
                          DocumentWindow::allButtons, 
                          true)
    {
        if (WORKCOLORS)
        {
		    //boring, work colors
		    theme.bg=Colour(240,240,240);
		    theme.button=Colours::grey;
		    theme.border=Colours::grey;
		    theme.borderhigh=theme.border.brighter();
		    theme.mapbg=Colours::white;
		    theme.grid=Colours::grey;
		    theme.text=Colours::grey;
		    theme.hexcursorcolor=Colours::white.darker();
            theme.maptext=Colours::navy;
            theme.hexdisputedcolor=Colours::grey.darker();
            theme.hexanchorcolor=Colours::aliceblue;
            theme.hexpathcolor=Colours::blanchedalmond;
            theme.hexpathredcolor=Colours::goldenrod;
        }
        else
        {
		    //fun, real colors
		    theme.bg=Colour(15,7,15);
		    theme.button=Colours::orange;
		    theme.border=Colours::indigo;
		    theme.borderhigh=theme.border.brighter();
		    theme.mapbg=Colours::black;
		    theme.grid=Colour(0,80,0);
		    theme.text=Colours::ivory;
		    theme.hexcursorcolor=Colours::red;
            theme.maptext=Colours::salmon.brighter();
            theme.hexdisputedcolor=Colours::beige;
            theme.hexanchorcolor=Colours::blue;
            theme.hexpathcolor=Colours::steelblue;
            theme.hexpathredcolor=Colours::tomato;
        }

		setContentComponent (new HelloWorldContentComponent(&theme));
        setVisible (true);

        int bt=Desktop::getInstance().getMainMonitorArea(true).getBottom();
        int lf=Desktop::getInstance().getMainMonitorArea(true).getX();

        int ht,wd;
        if (WORKCOLORS) {ht=200; wd=310;}
        else {ht=300; wd=310;}

        setBounds(lf+5,bt-(ht+5),wd,ht);

        setUsingNativeTitleBar(true);
        setResizable(true,true);
    }

    ~HelloWorldWindow()
    {
        // (the content component will be deleted automatically, so no need to do it here)
    }

    //==============================================================================
    void closeButtonPressed()
    {
        // When the user presses the close button, we'll tell the app to quit. This 
        // window will be deleted by the app object as it closes down.
        JUCEApplication::quit();
    }

};


//==============================================================================
/** This is the application object that is started up when Juce starts. It handles
    the initialisation and shutdown of the whole application.
*/
class JUCEHelloWorldApplication : public JUCEApplication
{
    /* Important! NEVER embed objects directly inside your JUCEApplication class! Use
       ONLY pointers to objects, which you should create during the initialise() method
       (NOT in the constructor!) and delete in the shutdown() method (NOT in the
       destructor!)

       This is because the application object gets created before Juce has been properly
       initialised, so any embedded objects would also get constructed too soon.
   */
    HelloWorldWindow* helloWorldWindow;

public:
    //==============================================================================
    JUCEHelloWorldApplication()
        : helloWorldWindow (0)
    {
        // NEVER do anything in here that could involve any Juce function being called
        // - leave all your startup tasks until the initialise() method.
    }

    ~JUCEHelloWorldApplication()
    {
        // Your shutdown() method should already have done all the things necessary to
        // clean up this app object, so you should never need to put anything in
        // the destructor.

        // Making any Juce calls in here could be very dangerous...
    }

    //==============================================================================
    void initialise (const String& commandLine)
    {
        // just create the main window...
        helloWorldWindow = new HelloWorldWindow();

        /*  ..and now return, which will fall into to the main event
            dispatch loop, and this will run until something calls
            JUCEAppliction::quit().

            In this case, JUCEAppliction::quit() will be called by the
            hello world window being clicked.
        */
    }

    void shutdown()
    {
        // clear up..

        if (helloWorldWindow != 0)
            delete helloWorldWindow;
    }

    //==============================================================================
    const String getApplicationName()
    {
        return T("Hello World for JUCE");
    }

    const String getApplicationVersion()
    {
        return T("1.0");
    }

    bool moreThanOneInstanceAllowed()
    {
        return true;
    }

    void anotherInstanceStarted (const String& commandLine)
    {
    }
};


//==============================================================================
// This macro creates the application's main() function..
START_JUCE_APPLICATION (JUCEHelloWorldApplication)
