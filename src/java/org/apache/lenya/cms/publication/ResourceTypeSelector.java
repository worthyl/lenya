/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.cocoon.components.ExtendedComponentSelector;

public class ResourceTypeSelector extends ExtendedComponentSelector{

    /**
     * @see org.apache.cocoon.components.ExtendedComponentSelector#select(java.lang.Object)
     */
    public Component select(Object hint) throws ComponentException {
        ResourceType type = (ResourceType) super.select(hint);
        if (type != null) {
            type.setName((String) hint);
        }
        return (Component) type;
    }

}
