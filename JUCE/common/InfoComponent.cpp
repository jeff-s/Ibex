#include "InfoComponent.h"
#include "Utils.h"

InfoComponent::InfoComponent(Theme* t)
{
    theme=t;
    l=new Label(T("InfoInfo"),String::empty);
    l->setBounds(1,1,1,1);
    //l->setColour(Label::outlineColourId,theme.button);
    l->setColour(Label::textColourId,theme->text.brighter());
	addMouseListener(this,true);
    addAndMakeVisible(l);
    ld=new Label(T("detail"),String::empty);
    ld->setBounds(1,1,1,1);
    ld->setColour(Label::outlineColourId,theme->button.brighter());
    ld->setColour(Label::textColourId,theme->text.darker());
    addAndMakeVisible(ld);
}

InfoComponent::~InfoComponent(void)
{
    deleteAllChildren();
}

void InfoComponent::paint(Graphics& g)
{
    g.setColour(theme->bg);
    g.fillAll();
    int cw=this->getWidth();
    int ch=this->getHeight();
    g.setColour(theme->border);
    g.fillRoundedRectangle(1.0,1.0,(float)(cw-2),(float)(ch-2),7.0);
    g.setColour(theme->border.brighter());
    g.fillRoundedRectangle(2.0,2.0,(float)(cw-4),(float)(ch-4),7.0);
    g.setColour(theme->border);
    g.fillRoundedRectangle(3.0,3.0,(float)(cw-6),(float)(ch-6),7.0);
    g.setColour(theme->mapbg);
    g.fillRoundedRectangle(4.0,4.0,(float)(cw-8),(float)(ch-8),7.0);
    g.setColour(theme->border);
    g.fillRoundedRectangle(3.0,3.0,(float)(cw-6),22.0,7.0);
}

void InfoComponent::resized()
{
    int cw=this->getWidth();
    int ch=this->getHeight();
    l->setBounds(5,5,cw-10,20);
    ld->setBounds(5,30,cw-10,60);
}

void InfoComponent::mouseDown(const MouseEvent& e)
{
	drag.startDraggingComponent(this,0);
}

void InfoComponent::mouseDrag(const MouseEvent& e)
{
	drag.dragComponent(this,e);
	//xp=(float)e.x/(float)getWidth();
	//yp=(float)e.y/(float)getHeight();
}

void InfoComponent::setTopText(const String& s)
{
    l->setText(s,true);
}

void InfoComponent::setD(const String& s)
{
    ld->setText(s,true);
}