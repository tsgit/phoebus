/*******************************************************************************
 * Copyright (c) 2015-2022 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.csstudio.display.builder.editor.undo;

import org.csstudio.display.builder.model.ArrayWidgetProperty;
import org.csstudio.display.builder.model.StructuredWidgetProperty;
import org.csstudio.display.builder.model.WidgetProperty;
import org.phoebus.ui.undo.UndoableAction;

/** Action to update 'use_class' of property
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class UseClassAction extends UndoableAction
{
    private final WidgetProperty<?> property;
    private final boolean use_class;

    /** @param widget_property WidgetProperty
     *  @param use_class Use class?
     */
    public UseClassAction(final WidgetProperty<?> widget_property, final boolean use_class)
    {
        super(widget_property.getName() + (use_class ? " - use widget class" : " - ignore widget class"));
        this.property = widget_property;
        this.use_class = use_class;
    }

    @Override
    public void run()
    {
        useClassDeep(property, use_class);
    }

    @Override
    public void undo()
    {
        useClassDeep(property, !use_class);
    }

    /** Set the use-class flag on property and descend into array and structure elements
     *  @param prop Property
     *  @param use Use class settings?
     */
    @SuppressWarnings("unchecked")
    private void useClassDeep(final WidgetProperty<?> prop, final boolean use)
    {
        prop.useWidgetClass(use);
        if (prop instanceof ArrayWidgetProperty<?>)
        {
            final ArrayWidgetProperty<WidgetProperty<?>> arr = (ArrayWidgetProperty<WidgetProperty<?>>) prop;
            for (WidgetProperty<?> element : arr.getValue())
                useClassDeep(element, use);
        }
        else if (prop instanceof StructuredWidgetProperty)
        {
            final StructuredWidgetProperty struct = (StructuredWidgetProperty) prop;
            for (WidgetProperty<?> element : struct.getValue())
                useClassDeep(element, use);
        }
    }
}
