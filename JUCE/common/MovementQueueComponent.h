// # pragma once

#ifndef _MOVEMENTQUEUECOMPONENT_H_
#define _MOVEMENTQUEUECOMPONENT_H_


#include "../../../juce.h"
#include "Utils.h"

class MovementQueueComponent : public Component, public ListBoxModel
{
public:
    MovementQueueComponent::MovementQueueComponent(Theme* t);
    MovementQueueComponent::~MovementQueueComponent(void);
    void paint(Graphics& g);
    void resized();
    void mouseDown (const MouseEvent& e);
    void mouseDrag (const MouseEvent& e);
    void setTopText(const String& s);
    void setD(const String& s);
    int getNumRows();
    void paintListBoxItem(int rowNumber, Graphics& g, int width, int height, bool rowIsSelected);
    void add(const String& it);

private:
    Theme *theme;
    Label *l;
    ListBox *lb;
    ComponentDragger drag;
    int count;
    OwnedArray<String> listItems;
};

#endif