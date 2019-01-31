package com.vaadin.flow.component.gridpro.examples;

import com.vaadin.flow.component.gridpro.EditColumnConfigurator;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route
public class MainView extends VerticalLayout {

    public MainView() {
        createEditorColumns();
    }

    protected void createEditorColumns() {
        Div itemDisplayPanel = new Div();
        Div subPropertyDisplayPanel = new Div();

        GridPro<Person> grid = new GridPro<>();
        grid.setItems(createItems());

        grid.addColumn(Person::getAge).setHeader("NAME");

        grid.addEditColumn(Person::getName, EditColumnConfigurator.text((Person item, String newValue) -> {
            itemDisplayPanel.setText(item.toString());
            subPropertyDisplayPanel.setText(newValue);
        })).setHeader("Name").setWidth("300px");

        grid.addEditColumn(Person::isSubscriber, EditColumnConfigurator.checkbox((Person item, Boolean newValue) -> {
            itemDisplayPanel.setText(item.toString());
            subPropertyDisplayPanel.setText(newValue.toString());
        })).setHeader("Subscriber").setWidth("300px");

        List<String> listOptions = new ArrayList<>();
        listOptions.add("Male");
        listOptions.add("Female");
        listOptions.add("Unknown");
        grid.addEditColumn(Person::getGender, EditColumnConfigurator.select((item, newValue) -> {
            itemDisplayPanel.setText(item.toString());
            subPropertyDisplayPanel.setText(newValue);
        }, listOptions)).setHeader("Gender").setWidth("300px");

        add(grid, itemDisplayPanel, subPropertyDisplayPanel);
    }

    private static List<Person> createItems() {
        Random random = new Random(0);
        return IntStream.range(1, 500)
                .mapToObj(index -> createPerson(index, random))
                .collect(Collectors.toList());
    }

    private static Person createPerson(int index, Random random) {
        Person person = new Person();
        person.setId(index);
        person.setEmail("person" + index + "@vaadin.com");
        person.setName("Person " + index);
        person.setAge(13 + random.nextInt(50));
        person.setGender(Gender.getRandomGender());

        return person;
    }
}
