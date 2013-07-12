#include "MovementQueueComponent.h"
#include "Utils.h"

MovementQueueComponent::MovementQueueComponent(Theme* t)
{
    theme=t;
    l=new Label(T("MQ"),String::empty);
    l->setBounds(1,1,1,1);
    //l->setColour(Label::outlineColourId,theme.button);
    l->setColour(Label::textColourId,theme->text.brighter());
    addMouseListener(this,true);
    addAndMakeVisible(l);
    lb=new ListBox(T("detail"),this);
    lb->setBounds(1,1,1,1);
    lb->setColour(ListBox::outlineColourId,theme->text.darker());
    lb->setColour(ListBox::textColourId,theme->text.darker());
    lb->setRowHeight(13);
    lb->updateContent();
    addAndMakeVisible(lb);
}

MovementQueueComponent::~MovementQueueComponent(void)
{
    deleteAllChildren();
}

void MovementQueueComponent::paint(Graphics& g)
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

void MovementQueueComponent::resized()
{
    int cw=this->getWidth();
    int ch=this->getHeight();
    l->setBounds(5,5,cw-10,20);
    lb->setBounds(5,30,cw-10,60);
}

void MovementQueueComponent::mouseDown(const MouseEvent& e)
{
    drag.startDraggingComponent(this,0);
}

void MovementQueueComponent::mouseDrag(const MouseEvent& e)
{
    drag.dragComponent(this,e);
}

void MovementQueueComponent::setTopText(const String& s)
{
    l->setText(s,true);
}

void MovementQueueComponent::setD(const String& s)
{
    //ld->setText(s,true);
}

int MovementQueueComponent::getNumRows()
{
    return listItems.size();
}

void MovementQueueComponent::paintListBoxItem(int rowNumber, Graphics& g, int width, int height, bool rowIsSelected)
{
    if (rowIsSelected) g.fillAll(theme->button.brighter());
    g.drawText (*listItems[rowNumber],
                        4, 0, width - 4, height,
                        Justification::centredLeft, true);
}

void MovementQueueComponent::add(const String& it)
{
    listItems.add(new String(it));
    lb->updateContent();
}

//void MovementQueueComponenet::populator(const MapComponent& c)
//{
//    listItems.clear();
//    for (int i=0;i<c->
