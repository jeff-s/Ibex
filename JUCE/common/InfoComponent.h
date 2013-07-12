// # pragma once

#ifndef _INFOCOMPONENT_H_
#define _INFOCOMPONENT_H_


#include "../../../juce.h"
#include "Utils.h"

class InfoComponent : public Component
{
public:
    InfoComponent::InfoComponent(Theme* t);
    InfoComponent::~InfoComponent(void);
    void paint(Graphics& g);
    void resized();
	void mouseDown (const MouseEvent& e);
	void mouseDrag (const MouseEvent& e);
    void setTopText(const String& s);
    void setD(const String& s);

	//float xp, yp;

private:
    Theme *theme;
    Label *l;
    Label *ld;
	ComponentDragger drag;
};

#endif