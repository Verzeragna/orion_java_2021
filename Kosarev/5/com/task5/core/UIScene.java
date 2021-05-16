package com.task5.core;

import com.task5.core.Elements.UIElement;
import com.task5.core.Exceptions.UIElementOverlapException;
import com.task5.core.Exceptions.UIInvalidSizeException;

import java.util.*;
import java.util.stream.Collectors;

public class UIScene {
    private static final int DEFAULT_WIDTH      = 100;
    private static final int DEFAULT_HEIGHT     = 100;
    private static final String TYPE_NAME       = "UIScene";
    private static final String UL_CORNER_CHAR  = "╔";
    private static final String UR_CORNER_CHAR  = "╗";
    private static final String DL_CORNER_CHAR  = "╚";
    private static final String DR_CORNER_CHAR  = "╝";
    private static final String VER_BORDER_CHAR = "║";
    private static final String HOR_BORDER_CHAR = "═";
    private static final String SPACE_CHAR      = " ";
    private static final String ENDL_CHAR       = "\n";

    private final int width;
    private final int height;
    private List<UIElement> elements;

    public void render() {
//        for (UIElement element : elements) {
//            System.out.println(element.toUISceneView().replaceAll("%endl%", "\n"));
//        }

        // Upper border
        int typenameStartX = (int) Math.floor( (width - TYPE_NAME.length())/2 );
        String sceneView = UL_CORNER_CHAR + HOR_BORDER_CHAR.repeat(typenameStartX - 1) + TYPE_NAME + HOR_BORDER_CHAR.repeat(typenameStartX - 1);
        if (sceneView.length() != width - 1)
            sceneView += HOR_BORDER_CHAR;
        sceneView += UR_CORNER_CHAR + ENDL_CHAR;

        // Body
        // TODO: body
        for (int y = 1; y < height; y++) {
            String ystr = VER_BORDER_CHAR;
            int finalY = y;
            List<UIElement> elementsOnCurrentY = elements.stream()
                    .filter(e -> (
                            finalY >= e.getY() &&
                            finalY < e.getY() + e.getHeight() ))
                    .sorted(Comparator.comparing(UIElement::getX))
                    .collect(Collectors.toList());
            for (UIElement e : elementsOnCurrentY) {
                ystr += SPACE_CHAR.repeat(e.getX() - ystr.length())
                        + e.toUISceneView().split(UIElement.getEndlChar())[y - e.getY()];
            }
            ystr += SPACE_CHAR.repeat(width - ystr.length() - 1);
            ystr += VER_BORDER_CHAR + ENDL_CHAR;
            sceneView += ystr;
        }

        // Bottom border
        sceneView += DL_CORNER_CHAR + HOR_BORDER_CHAR.repeat(width-2) + DR_CORNER_CHAR + ENDL_CHAR;
        System.out.println(sceneView);
    }

    public void addElement(UIElement newElement) throws UIElementOverlapException {
        if (isElementsOverlapping(newElement, elements))
            throw new UIElementOverlapException("Element " + newElement.toString() + " is overlapping someone");  // - не очень хорошо, т.к. не знаем точно с кем пересекается newElement
        elements.add(newElement);
    }

    public void delElement(UIElement element) {
        elements.remove(element);
    }

    public List<UIElement> getElements() {
        return elements;
    }

    public UIScene() throws UIInvalidSizeException {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public UIScene(int width, int height) throws UIInvalidSizeException {
        if (isSizeInvalid(width) || isSizeInvalid(height))
            throw new UIInvalidSizeException(width, height);
        this.width = width;
        this.height = height;
        this.elements = new LinkedList<>();
    }

    private boolean isSizeInvalid(int widthOrHeight) {
        return widthOrHeight <= 0;
    }

    private boolean isElementsOverlapping(UIElement e1, UIElement e2) {
        return e1.getX() + e1.getWidth() >= e2.getX() ||
                e1.getY() + e1.getHeight() >= e2.getY() ||
                e2.getX() + e2.getWidth() >= e1.getX() ||
                e2.getY() + e2.getHeight() >= e1.getY();
    }

    private boolean isElementsOverlapping(UIElement e, List<UIElement> elements) {
        if (elements.isEmpty())
            return false;

        boolean overlapping = false;
        for (UIElement element : elements) {
            overlapping &= isElementsOverlapping(e, element);
            if (overlapping)
                return true;
        }
        return false;
    }
}