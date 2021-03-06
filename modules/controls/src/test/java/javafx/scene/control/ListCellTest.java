/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.scene.control;

import com.sun.javafx.scene.control.infrastructure.StageLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import com.sun.javafx.stage.EmbeddedWindow;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import static org.junit.Assert.*;

/**
 */
public class ListCellTest {
    private ListCell<String> cell;
    private ListView<String> list;
    private ObservableList<String> model;

    @Before public void setup() {
        cell = new ListCell<String>();
        model = FXCollections.observableArrayList("Apples", "Oranges", "Pears");
        list = new ListView<String>(model);
    }

    /*********************************************************************
     * Tests for the constructors                                        *
     ********************************************************************/

    @Test public void styleClassIs_list_cell_byDefault() {
        assertStyleClassContains(cell, "list-cell");
    }

    // The item should be null by default because the index is -1 by default
    @Test public void itemIsNullByDefault() {
        assertNull(cell.getItem());
    }

    /*********************************************************************
     * Tests for the listView property                                   *
     ********************************************************************/

    @Test public void listViewIsNullByDefault() {
        assertNull(cell.getListView());
        assertNull(cell.listViewProperty().get());
    }

    @Test public void updateListViewUpdatesListView() {
        cell.updateListView(list);
        assertSame(list, cell.getListView());
        assertSame(list, cell.listViewProperty().get());
    }

    @Test public void canSetListViewBackToNull() {
        cell.updateListView(list);
        cell.updateListView(null);
        assertNull(cell.getListView());
        assertNull(cell.listViewProperty().get());
    }

    @Test public void listViewPropertyReturnsCorrectBean() {
        assertSame(cell, cell.listViewProperty().getBean());
    }

    @Test public void listViewPropertyNameIs_listView() {
        assertEquals("listView", cell.listViewProperty().getName());
    }

    @Test public void updateListViewWithNullFocusModelResultsInNoException() {
        cell.updateListView(list);
        list.setFocusModel(null);
        cell.updateListView(new ListView());
    }

    @Test public void updateListViewWithNullFocusModelResultsInNoException2() {
        list.setFocusModel(null);
        cell.updateListView(list);
        cell.updateListView(new ListView());
    }

    @Test public void updateListViewWithNullFocusModelResultsInNoException3() {
        cell.updateListView(list);
        ListView list2 = new ListView();
        list2.setFocusModel(null);
        cell.updateListView(list2);
    }

    @Test public void updateListViewWithNullSelectionModelResultsInNoException() {
        cell.updateListView(list);
        list.setSelectionModel(null);
        cell.updateListView(new ListView());
    }

    @Test public void updateListViewWithNullSelectionModelResultsInNoException2() {
        list.setSelectionModel(null);
        cell.updateListView(list);
        cell.updateListView(new ListView());
    }

    @Test public void updateListViewWithNullSelectionModelResultsInNoException3() {
        cell.updateListView(list);
        ListView list2 = new ListView();
        list2.setSelectionModel(null);
        cell.updateListView(list2);
    }

    @Test public void updateListViewWithNullItemsResultsInNoException() {
        cell.updateListView(list);
        list.setItems(null);
        cell.updateListView(new ListView());
    }

    @Test public void updateListViewWithNullItemsResultsInNoException2() {
        list.setItems(null);
        cell.updateListView(list);
        cell.updateListView(new ListView());
    }

    @Test public void updateListViewWithNullItemsResultsInNoException3() {
        cell.updateListView(list);
        ListView list2 = new ListView();
        list2.setItems(null);
        cell.updateListView(list2);
    }

    /*********************************************************************
     * Tests for the item property. It should be updated whenever the    *
     * index, or listView changes, including the listView's items.       *
     ********************************************************************/

    @Test public void itemMatchesIndexWithinListItems() {
        cell.updateIndex(0);
        cell.updateListView(list);
        assertSame(model.get(0), cell.getItem());
        cell.updateIndex(1);
        assertSame(model.get(1), cell.getItem());
    }

    @Test public void itemMatchesIndexWithinListItems2() {
        cell.updateListView(list);
        cell.updateIndex(0);
        assertSame(model.get(0), cell.getItem());
        cell.updateIndex(1);
        assertSame(model.get(1), cell.getItem());
    }

    @Test public void itemIsNullWhenIndexIsOutOfRange() {
        cell.updateIndex(50);
        cell.updateListView(list);
        assertNull(cell.getItem());
    }

    @Test public void itemIsNullWhenIndexIsOutOfRange2() {
        cell.updateListView(list);
        cell.updateIndex(50);
        assertNull(cell.getItem());
    }

    // Above were the simple tests. Now we check various circumstances
    // to make sure the item is updated correctly.

    @Test public void itemIsUpdatedWhenItWasOutOfRangeButUpdatesToListViewItemsMakesItInRange() {
        cell.updateIndex(4);
        cell.updateListView(list);
        model.addAll("Pumpkin", "Lemon");
        assertSame(model.get(4), cell.getItem());
    }

    @Test public void itemIsUpdatedWhenItWasInRangeButUpdatesToListViewItemsMakesItOutOfRange() {
        cell.updateIndex(2);
        cell.updateListView(list);
        assertSame(model.get(2), cell.getItem());
        model.remove(2);
        assertNull(cell.getItem());
    }

    @Test public void itemIsUpdatedWhenListViewItemsIsUpdated() {
        cell.updateIndex(1);
        cell.updateListView(list);
        assertSame(model.get(1), cell.getItem());
        model.set(1, "Lime");
        assertEquals("Lime", cell.getItem());
    }

    @Test public void itemIsUpdatedWhenListViewItemsHasNewItemInsertedBeforeIndex() {
        cell.updateIndex(1);
        cell.updateListView(list);
        assertSame(model.get(1), cell.getItem());
        String previous = model.get(0);
        model.add(0, "Lime");
        assertEquals(previous, cell.getItem());
    }

    @Test public void itemIsUpdatedWhenListViewItemsHasItemRemovedBeforeIndex() {
        cell.updateIndex(1);
        cell.updateListView(list);
        assertSame(model.get(1), cell.getItem());
        String other = model.get(2);
        model.remove(0);
        assertEquals(other, cell.getItem());
    }

    @Test public void itemIsUpdatedWhenListViewItemsIsReplaced() {
        ObservableList<String> model2 = FXCollections.observableArrayList("Water", "Juice", "Soda");
        cell.updateIndex(1);
        cell.updateListView(list);
        list.setItems(model2);
        assertEquals("Juice", cell.getItem());
    }

    @Test public void itemIsUpdatedWhenListViewIsReplaced() {
        cell.updateIndex(2);
        cell.updateListView(list);
        ObservableList<String> model2 = FXCollections.observableArrayList("Water", "Juice", "Soda");
        ListView<String> listView2 = new ListView<String>(model2);
        cell.updateListView(listView2);
        assertEquals("Soda", cell.getItem());
    }

    @Test public void replaceItemsWithANull() {
        cell.updateIndex(0);
        cell.updateListView(list);
        list.setItems(null);
        assertNull(cell.getItem());
    }

    @Test public void replaceItemsWithANull_ListenersRemovedFromFormerList() {
        cell.updateIndex(0);
        cell.updateListView(list);
        ListChangeListener listener = getListChangeListener(cell, "weakItemsListener");
        assertListenerListContains(model, listener);
        list.setItems(null);
        assertListenerListDoesNotContain(model, listener);
    }

    @Test public void replaceANullItemsWithNotNull() {
        cell.updateIndex(0);
        cell.updateListView(list);
        list.setItems(null);
        ObservableList<String> model2 = FXCollections.observableArrayList("Water", "Juice", "Soda");
        list.setItems(model2);
        assertEquals("Water", cell.getItem());
    }

    /*********************************************************************
     * Tests for the selection listener                                  *
     ********************************************************************/

    @Test public void selectionOnSelectionModelIsReflectedInCells() {
        cell.updateListView(list);
        cell.updateIndex(0);

        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        list.getSelectionModel().selectFirst();
        assertTrue(cell.isSelected());
        assertFalse(other.isSelected());
    }

    @Test public void changesToSelectionOnSelectionModelAreReflectedInCells() {
        cell.updateListView(list);
        cell.updateIndex(0);

        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        // Because the ListView is in single selection mode, calling
        // selectNext causes a loss of focus for the first cell.
        list.getSelectionModel().selectFirst();
        list.getSelectionModel().selectNext();
        assertFalse(cell.isSelected());
        assertTrue(other.isSelected());
    }

    @Test public void replacingTheSelectionModelCausesSelectionOnCellsToBeUpdated() {
        // Cell is configured to represent row 0, which is selected.
        cell.updateListView(list);
        cell.updateIndex(0);
        list.getSelectionModel().select(0);

        // Other is configured to represent row 1 which is not selected.
        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        // The replacement selection model has row 1 selected, not row 0
        MultipleSelectionModel<String> selectionModel = new SelectionModelMock();
        selectionModel.select(1);

        list.setSelectionModel(selectionModel);
        assertFalse(cell.isSelected());
        assertTrue(other.isSelected());
    }

    @Test public void changesToSelectionOnSelectionModelAreReflectedInCells_MultipleSelection() {
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cell.updateListView(list);
        cell.updateIndex(0);

        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        list.getSelectionModel().selectFirst();
        list.getSelectionModel().selectNext();
        assertTrue(cell.isSelected());
        assertTrue(other.isSelected());
    }

    @Test public void replacingTheSelectionModelCausesSelectionOnCellsToBeUpdated_MultipleSelection() {
        // Cell is configured to represent row 0, which is selected.
        cell.updateListView(list);
        cell.updateIndex(0);
        list.getSelectionModel().select(0);

        // Other is configured to represent row 1 which is not selected.
        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        // The replacement selection model has row 0 and 1 selected
        MultipleSelectionModel<String> selectionModel = new SelectionModelMock();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        selectionModel.selectIndices(0, 1);

        list.setSelectionModel(selectionModel);
        assertTrue(cell.isSelected());
        assertTrue(other.isSelected());
    }

    @Test public void replaceANullSelectionModel() {
        // Cell is configured to represent row 0, which is selected.
        list.setSelectionModel(null);
        cell.updateIndex(0);
        cell.updateListView(list);

        // Other is configured to represent row 1 which is not selected.
        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        // The replacement selection model has row 1 selected
        MultipleSelectionModel<String> selectionModel = new SelectionModelMock();
        selectionModel.select(1);

        list.setSelectionModel(selectionModel);
        assertFalse(cell.isSelected());
        assertTrue(other.isSelected());
    }

    @Test public void setANullSelectionModel() {
        // Cell is configured to represent row 0, which is selected.
        cell.updateIndex(0);
        cell.updateListView(list);

        // Other is configured to represent row 1 which is not selected.
        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        // Replace with a null selection model, which should clear selection
        list.setSelectionModel(null);
        assertFalse(cell.isSelected());
        assertFalse(other.isSelected());
    }

    @Ignore @Test public void replacingTheSelectionModelRemovesTheListenerFromTheOldModel() {
        cell.updateIndex(0);
        cell.updateListView(list);
        MultipleSelectionModel<String> sm = list.getSelectionModel();
        ListChangeListener listener = getListChangeListener(cell, "weakSelectedListener");
        assertListenerListContains(sm.getSelectedIndices(), listener);
        list.setSelectionModel(new SelectionModelMock());
        assertListenerListDoesNotContain(sm.getSelectedIndices(), listener);
    }

    /*********************************************************************
     * Tests for the focus listener                                      *
     ********************************************************************/

    @Test public void focusOnFocusModelIsReflectedInCells() {
        cell.updateListView(list);
        cell.updateIndex(0);

        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        list.getFocusModel().focus(0);
        assertTrue(cell.isFocused());
        assertFalse(other.isFocused());
    }

    @Test public void changesToFocusOnFocusModelAreReflectedInCells() {
        cell.updateListView(list);
        cell.updateIndex(0);

        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        list.getFocusModel().focus(0);
        list.getFocusModel().focus(1);
        assertFalse(cell.isFocused());
        assertTrue(other.isFocused());
    }

    @Test public void replacingTheFocusModelCausesFocusOnCellsToBeUpdated() {
        // Cell is configured to represent row 0, which is focused.
        cell.updateListView(list);
        cell.updateIndex(0);
        list.getFocusModel().focus(0);

        // Other is configured to represent row 1 which is not focused.
        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        // The replacement focus model has row 1 selected, not row 0
        FocusModel<String> focusModel = new FocusModelMock();
        focusModel.focus(1);

        list.setFocusModel(focusModel);
        assertFalse(cell.isFocused());
        assertTrue(other.isFocused());
    }

    @Test public void replaceANullFocusModel() {
        // Cell is configured to represent row 0, which is focused.
        list.setFocusModel(null);
        cell.updateIndex(0);
        cell.updateListView(list);

        // Other is configured to represent row 1 which is not focused
        ListCell<String> other = new ListCell<String>();
        other.updateListView(list);
        other.updateIndex(1);

        // The replacement focus model has row 1 focused
        FocusModel<String> focusModel = new FocusModelMock();
        focusModel.focus(1);

        list.setFocusModel(focusModel);
        assertFalse(cell.isFocused());
        assertTrue(other.isFocused());
    }

    @Test public void setANullFocusModel() {
        // Cell is configured to represent row 0, which is focused.
        cell.updateIndex(0);
        cell.updateListView(list);

        // Other is configured to represent row 1 which is not focused.
        ListCell<String> other = new ListCell<>();
        other.updateListView(list);
        other.updateIndex(1);

        // Replace with a null focus model, which should clear focused
        list.setFocusModel(null);
        assertFalse(cell.isFocused());
        assertFalse(other.isFocused());
    }

    @Test public void replacingTheFocusModelRemovesTheListenerFromTheOldModel() {
        cell.updateIndex(0);
        cell.updateListView(list);
        FocusModel<String> fm = list.getFocusModel();
        InvalidationListener listener = getInvalidationListener(cell, "weakFocusedListener");
        assertValueListenersContains(fm.focusedIndexProperty(), listener);
        list.setFocusModel(new FocusModelMock());
        assertValueListenersDoesNotContain(fm.focusedIndexProperty(), listener);
    }

    /*********************************************************************
     * Tests for all things related to editing one of these guys         *
     ********************************************************************/

    // startEdit()
    @Test public void editOnListViewResultsInEditingInCell() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        list.edit(1);
        assertTrue(cell.isEditing());
    }

    @Test public void editOnListViewResultsInNotEditingInCellWhenDifferentIndex() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        list.edit(0);
        assertFalse(cell.isEditing());
    }

    @Test public void editCellWithNullListViewResultsInNoExceptions() {
        cell.updateIndex(1);
        cell.startEdit();
    }

    @Test public void editCellOnNonEditableListDoesNothing() {
        cell.updateIndex(1);
        cell.updateListView(list);
        cell.startEdit();
        assertFalse(cell.isEditing());
        assertEquals(-1, list.getEditingIndex());
    }

    @Test public void editCellWithListResultsInUpdatedEditingIndexProperty() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        cell.startEdit();
        assertEquals(1, list.getEditingIndex());
    }

    @Test public void editCellFiresEventOnList() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(2);
        final boolean[] called = new boolean[] { false };
        list.setOnEditStart(event -> {
            called[0] = true;
        });
        cell.startEdit();
        assertTrue(called[0]);
    }

    // commitEdit()
    @Test public void commitWhenListIsNullIsOK() {
        cell.updateIndex(1);
        cell.startEdit();
        cell.commitEdit("Watermelon");
    }

    @Test public void commitWhenListIsNotNullWillUpdateTheItemsList() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        cell.startEdit();
        cell.commitEdit("Watermelon");
        assertEquals("Watermelon", list.getItems().get(1));
    }

    @Test public void commitSendsEventToList() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        cell.startEdit();
        final boolean[] called = new boolean[] { false };
        list.setOnEditCommit(event -> {
            called[0] = true;
        });
        cell.commitEdit("Watermelon");
        assertTrue(called[0]);
    }

    @Test public void afterCommitListViewEditingIndexIsNegativeOne() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        cell.startEdit();
        cell.commitEdit("Watermelon");
        assertEquals(-1, list.getEditingIndex());
        assertFalse(cell.isEditing());
    }

    // cancelEdit()
    @Test public void cancelEditCanBeCalledWhileListViewIsNull() {
        cell.updateIndex(1);
        cell.startEdit();
        cell.cancelEdit();
    }

    @Test public void cancelEditFiresChangeEvent() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        cell.startEdit();
        final boolean[] called = new boolean[] { false };
        list.setOnEditCancel(event -> {
            called[0] = true;
        });
        cell.cancelEdit();
        assertTrue(called[0]);
    }

    @Test public void cancelSetsListViewEditingIndexToNegativeOne() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(1);
        cell.startEdit();
        cell.cancelEdit();
        assertEquals(-1, list.getEditingIndex());
        assertFalse(cell.isEditing());
    }

    @Test public void movingListCellEditingIndexCausesCurrentlyInEditCellToCancel() {
        list.setEditable(true);
        cell.updateListView(list);
        cell.updateIndex(0);
        cell.startEdit();

        ListCell other = new ListCell();
        other.updateListView(list);
        other.updateIndex(1);
        list.edit(1);

        assertTrue(other.isEditing());
        assertFalse(cell.isEditing());
    }

    // When the list view item's change and affects a cell that is editing, then what?
    // When the list cell's index is changed while it is editing, then what?


    private final class SelectionModelMock extends MultipleSelectionModelBase<String> {
        @Override protected int getItemCount() {
            return model.size();
        }

        @Override protected String getModelItem(int index) {
            return model.get(index);
        }

        @Override protected void focus(int index) {
            // no op
        }

        @Override protected int getFocusedIndex() {
            return list.getFocusModel().getFocusedIndex();
        }
    };

    private final class FocusModelMock extends FocusModel {
        @Override protected int getItemCount() {
            return model.size();
        }

        @Override protected Object getModelItem(int row) {
            return model.get(row);
        }
    }

    private int rt_29923_count = 0;
    @Test public void test_rt_29923() {
        // setup test
        cell = new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                rt_29923_count++;
            }
        };
        list.getItems().setAll(null, null, null);
        cell.updateListView(list);

        rt_29923_count = 0;
        cell.updateIndex(0);
        assertNull(cell.getItem());
        assertFalse(cell.isEmpty());
        assertEquals(1, rt_29923_count);

        cell.updateIndex(1);
        assertNull(cell.getItem());
        assertFalse(cell.isEmpty());

        // This test used to be as shown below....but due to RT-33108, it changed
        // to the enabled code beneath. Refer to the first comment in RT-33108
        // for more detail, but in short we can't optimise and not call updateItem
        // when the new and old items are the same - doing so means we can end
        // up with bad bindings, etc in the individual cells (in other words,
        // even if their item has not changed, the rest of their state may have)
//        assertEquals(1, rt_29923_count);    // even though the index has changed,
//                                            // the item is the same, so we don't
//                                            // update the cell item.
        assertEquals(2, rt_29923_count);
    }

    @Test public void test_rt_33106() {
        cell.updateListView(list);
        list.setItems(null);
        cell.updateIndex(1);
    }
}
