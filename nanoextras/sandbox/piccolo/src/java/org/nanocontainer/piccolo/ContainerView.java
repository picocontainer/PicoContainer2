/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.piccolo;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import org.picocontainer.PicoContainer;

import java.awt.Dimension;
import java.awt.geom.Point2D;

public class ContainerView extends PCanvas {

    private PicoContainer container;
    private PNode containerNode;
    private PNode lastSelection;
    private PText tooltipNode;

    public ContainerView(PicoContainer container) {
        this.container = container;

        this.tooltipNode = new PText();
        this.tooltipNode.setPickable(false);
        getCamera().addChild(tooltipNode);

        // Create the path for the root container
        this.containerNode = PiccoloHelper.createNodeForContainer(this.container, 0, 0, 300);
        this.getLayer().addChild(this.containerNode);

        // Auto Pan/Zoom
        this.getCamera().addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                PNode node = event.getPickedNode();
                if (node instanceof PCamera) {
                } else {
                    // Simulate selection by changing the stroke paint
                    if (lastSelection instanceof PPath) {
                        ((PPath) lastSelection).setStrokePaint(Constants.PICO_STROKE);
                    }
                    lastSelection = node;
                    if (lastSelection instanceof PPath) {
                        ((PPath) lastSelection).setStrokePaint(Constants.PICO_GREEN);
                    }

                    // Tag the event
                    //event.setHandled(true);

                    if (event.getClickCount() == 2) {
                        // Get the bounds
                        PBounds b = node.getGlobalFullBounds();
                        double offset = b.getWidth() / 20;
                        PBounds n = new PBounds(b.getX() - offset, b.getY() - offset, b.getWidth() + 2 * offset, b.getHeight() + 2 * offset);

                        // Animate to the bounds
// (AH - doesn't compile)                       getCamera().animateViewToCenterBounds(n, true, 500);
                        getCamera().animateViewToBounds(n, true, 500);
                    }
                }
            }

            public void mouseMoved(PInputEvent event) {
                updateToolTip(event);
            }

            public void mouseDragged(PInputEvent event) {
                updateToolTip(event);
            }

            public void updateToolTip(PInputEvent event) {
                PNode n = event.getInputManager().getMouseOver().getPickedNode();
                String tooltipString = (String) n.getClientProperty(Constants.TOOL_TIP);
                Point2D p = event.getCanvasPosition();

                event.getPath().canvasToLocal(p, getCamera());

                tooltipNode.setText(tooltipString);
                tooltipNode.setOffset(p.getX() + 8, p.getY() - 8);
            }
        });

        // No zoom handler
        this.removeInputEventListener(getZoomEventHandler());
        // No pan handler
        this.removeInputEventListener(getPanEventHandler());
    }

    public Dimension getMinimumSize() {
        return new Dimension((int) getLayer().getFullBounds().getWidth(), (int) getLayer().getFullBounds().getHeight());
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
}
