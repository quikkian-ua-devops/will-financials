/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.spring.datadictionary;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class KualiBeanDefinitionParserBase extends AbstractBeanDefinitionParser {

    private static Logger LOG = Logger.getLogger(KualiBeanDefinitionParserBase.class);

    protected void parseEmbeddedPropertyElements(Element element, BeanDefinitionBuilder bean) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getLocalName() != null && child.getLocalName().equals("property")) {
                Element propertyElement = (Element) child;
                String propName = propertyElement.getAttribute("name");
                String propValue = propertyElement.getAttribute("value");
                if (propValue != null) {
                    bean.addPropertyValue(propName, propValue);
                } else if (propertyElement.getAttribute("ref") != null) {
                    bean.addPropertyReference(propName, propertyElement.getAttribute("ref"));
                }
            }
        }
    }

    protected void handleAbstractAttribute(Element element, BeanDefinitionBuilder bean) {
        String abstractStr = element.getAttribute("abstract");

        if (StringUtils.hasText(abstractStr)) {
            bean.setAbstract(Boolean.valueOf(abstractStr));
        }
    }

    /* The below copied from AbstractSingleBeanDefinitionParser and modified to allow for parent beans to be handled. */

    /**
     * Creates a {@link BeanDefinitionBuilder} instance for the
     * {@link #getBeanClass bean Class} and passes it to the
     * {@link #doParse} strategy method.
     *
     * @param element       the element that is to be parsed into a single BeanDefinition
     * @param parserContext the object encapsulating the current state of the parsing process
     * @return the BeanDefinition resulting from the parsing of the supplied {@link Element}
     * @throws IllegalStateException if the bean {@link Class} returned from
     *                               {@link #getBeanClass(org.w3c.dom.Element)} is <code>null</code>
     * @see #doParse
     */
    @SuppressWarnings("unchecked")
    protected final AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = null;

        String parent = element.getAttribute("parent");
        String beanClass = element.getAttribute("class");
        if (StringUtils.hasText(beanClass)) {
            try {
                builder = BeanDefinitionBuilder.rootBeanDefinition(Class.forName(beanClass));
            } catch (Exception ex) {
                LOG.fatal("Unable to resolve class given in class element of a " + element.getLocalName() + " element with id " + element.getAttribute("id"), ex);
                throw new RuntimeException(ex);
            }
        } else if (StringUtils.hasText(parent)) {
            builder = BeanDefinitionBuilder.childBeanDefinition(parent);
        } else if (getBeanClass(element) != null) {
            builder = BeanDefinitionBuilder.rootBeanDefinition(getBeanClass(element));
        } else {
            builder = BeanDefinitionBuilder.childBeanDefinition(getBaseBeanTypeParent(element));
        }
        builder.setSource(parserContext.extractSource(element));
        if (parserContext.isNested()) {
            // Inner bean definition must receive same singleton status as containing bean.
            builder.setSingleton(parserContext.getContainingBeanDefinition().isSingleton());
        }
        if (parserContext.isDefaultLazyInit()) {
            // Default-lazy-init applies to custom bean definitions as well.
            builder.setLazyInit(true);
        }
        doParse(element, parserContext, builder);
        return builder.getBeanDefinition();
    }

    /**
     * Parse the supplied {@link Element} and populate the supplied
     * {@link BeanDefinitionBuilder} as required.
     * <p>The default implementation delegates to the <code>doParse</code>
     * version without ParserContext argument.
     *
     * @param element       the XML element being parsed
     * @param parserContext the object encapsulating the current state of the parsing process
     * @param builder       used to define the <code>BeanDefinition</code>
     * @see #doParse(Element, BeanDefinitionBuilder)
     */
    protected abstract void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder);

    protected abstract String getBaseBeanTypeParent(Element element);

    protected Class getBeanClass(Element element) {
        return null;
    }
}
