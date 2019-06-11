package com.vaadin.flow.component.gridpro.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

    public MainView() {
        createEditorColumns();
        createBeanGridWithEditColumns();
    }

    protected void createEditorColumns() {
        Div itemDisplayPanel = new Div();
        Div subPropertyDisplayPanel = new Div();
        subPropertyDisplayPanel.setId("prop-panel");

        Div eventsPanel = new Div();
        eventsPanel.setId("events-panel");

        GridPro<Person> grid = new GridPro<>();
        List<City> cityList = new ArrayList<>();
        List<Person> personList = createItems(cityList);
        grid.setItems(personList);

        grid.addCellEditStartedListener(e -> eventsPanel.add(e.getItem().toString()));

        grid.addColumn(Person::getAge).setHeader("Age");

        grid.addEditColumn(Person::getName, "name").text((item, newValue) -> {
            item.setName(newValue);
            itemDisplayPanel.setText(item.toString());
            subPropertyDisplayPanel.setText(newValue);
        }).setHeader("Name").setWidth("300px");

        ComboBox<Department> cb = new ComboBox<>();
        cb.setItems(Department.values());
        grid.addEditColumn(Person::getDepartment).custom(cb, (item, newValue) -> {
            item.setDepartment(newValue);
            itemDisplayPanel.setText(item.toString());
            subPropertyDisplayPanel.setText(String.valueOf(newValue));
        }).setHeader("Department").setWidth("300px");

        ComponentRenderer<Span, Person> booleanRenderer = new ComponentRenderer<>(person ->
            new Span(person.isSubscriber() ? "Yes" : "No")
        );
        grid.addEditColumn(Person::isSubscriber, booleanRenderer).checkbox((item, newValue) -> {
            item.setSubscriber(newValue);
            itemDisplayPanel.setText(item.toString());
            subPropertyDisplayPanel.setText(newValue.toString());
        }).setHeader("Subscriber").setWidth("300px");

        ComboBox<City> cityCb = new ComboBox<>();
        cityCb.setItems(cityList);
        cityCb.setItemLabelGenerator(City::getName);

        ComponentRenderer<Span, Person> cityRenderer =
            new ComponentRenderer<>(person -> {
                if (person.getCity() != null) {
                    return new Span(person.getCity().getName());
                } else {
                    return new Span("");
                }
            });

        grid.addEditColumn(Person::getCity, cityRenderer).custom(cityCb,
                (item, newValue) -> {
                    item.setCity(newValue);
                    newValue.setPerson(item);
                    itemDisplayPanel.setText(item.toString());
                    subPropertyDisplayPanel.setText(newValue.toString());
        }).setHeader("City").setWidth("300px");

        add(grid, itemDisplayPanel, subPropertyDisplayPanel, eventsPanel);
    }

    protected void createBeanGridWithEditColumns() {
        GridPro<Person> beanGrid = new GridPro<>(Person.class);
        beanGrid.setColumns();
        List<City> cityList = new ArrayList<>();
        List<Person> personList = createItems(cityList);
        beanGrid.setItems(personList);

        beanGrid.addEditColumn("age").text((item, newValue) -> item.setAge(Integer.valueOf(newValue)));

        TextField textField = new TextField();
        beanGrid.addEditColumn("name").custom(textField, (item, newValue) -> item.setName(newValue));

        List<String> listOptions = new ArrayList<>();
        listOptions.add("Services");
        listOptions.add("Marketing");
        listOptions.add("Sales");
        beanGrid.addEditColumn("department").select((item, newValue) -> {
            item.setDepartment(fromStringRepresentation((newValue)));
        }, listOptions).setHeader("Department").setWidth("300px");

        add(beanGrid);
    }

    private static List<Person> createItems(List<City> cityList) {
        Random random = new Random(0);
        return IntStream.range(1, 500)
                .mapToObj(index -> createPerson(index, random, cityList))
                .collect(Collectors.toList());
    }

    private static Person createPerson(int index, Random random,
                                       List<City> cityList) {
        Person person = new Person();
        person.setId(index);
        person.setEmail("person" + index + "@vaadin.com");
        person.setName("Person " + index);
        person.setAge(13 + random.nextInt(50));

        City city = new City("City" + index, person);
        city.setId(index);
        person.setCity(city);
        cityList.add(city);

        if (index == 1) {
            person.setDepartment(Department.SALES);
        } else {
            person.setDepartment(Department.getRandomDepartment());
        }

        return person;
    }

    public static Department fromStringRepresentation(String stringRepresentation)
    {
        for(Department type : Department.values()) {
            if(type.getStringRepresentation().equals(stringRepresentation.toLowerCase())) {
               return type;
            }
        }

        return null;
    }
}
