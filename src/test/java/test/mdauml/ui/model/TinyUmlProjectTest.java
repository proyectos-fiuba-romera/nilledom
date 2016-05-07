/**
 * Copyright 2007 Wei-ju Wu
 *
 * This file is part of TinyUML.
 *
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package test.mdauml.ui.model;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.nilledom.model.UmlDiagram;
import com.nilledom.model.UmlModel;
import com.nilledom.ui.model.Project;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class TinyUmlProjectTest extends MockObjectTestCase {

  /**
   * Tests setters and getters.
   */
  public void testSetters() {
    Mock mockModel = mock(UmlModel.class);
    Project project = new Project((UmlModel) mockModel.proxy());
    assertEquals(mockModel.proxy(), project.getModel());
    assertEquals(0, project.getOpenDiagrams().size());
    Mock mockDiagram0 = mock(UmlDiagram.class);
    Mock mockDiagram1 = mock(UmlDiagram.class);
    project.addOpenDiagram((UmlDiagram) mockDiagram0.proxy());
    project.addOpenDiagram((UmlDiagram) mockDiagram1.proxy());
    assertEquals(2, project.getOpenDiagrams().size());
    assertEquals(mockDiagram0.proxy(), project.getOpenDiagrams().get(0));
    assertEquals(mockDiagram1.proxy(), project.getOpenDiagrams().get(1));
  }
}
