package com.stibo.demo.report.service;

import com.stibo.demo.report.logging.LogTime;
import com.stibo.demo.report.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ReportService {

    List<List<String>> listOfRows = new ArrayList<>();

    @LogTime
    public Stream<Stream<String>> report(Datastandard datastandard, String categoryId) {
        // Add column titles to list
        listOfRows.add(createTitleRow());

        // Iterate through categories to create the data model
        for (Category category : datastandard.categories()){
            List<String> categoryList = new ArrayList<>();
            List<String> groupIds = new ArrayList<>();
            categoryList.add(category.name());

            // Iterate through attribute links and extract attribute data
            for (AttributeLink attributeLink : category.attributeLinks()){
                for (Attribute attribute : datastandard.attributes()){
                    if(attribute.id().equals(attributeLink.id())){
                        categoryList.add(handleNullValue(checkForOptionalFlagOnAttributeName(attributeLink, attribute)));
                        categoryList.add(handleNullValue(attribute.description()));
                        categoryList.add(handleNullValue(checkForMultiValueFlagOnAttributeType(attribute.type())));

                        // Read group ids, fetch group names and append linebreak to multivalue cells
                        for (AttributeGroup attributeGroup : datastandard.attributeGroups()){
                            attribute.groupIds().forEach(groupId ->
                                    { if( attributeGroup.id().equals(groupId)) groupIds.add(attributeGroup.name()); });
                        }
                        categoryList.add(String.join("\n", groupIds));
                    }
                }
            }


            listOfRows.add(categoryList);

        }

        return listOfRows.stream().map(Collection::stream);

    }

    private List<String> createTitleRow(){
        List<String> columnTitles= new ArrayList<>();
        columnTitles.add("Category Name");
        columnTitles.add("Attribute Name");
        columnTitles.add("Description");
        columnTitles.add("Type");
        columnTitles.add("Groups");
        return columnTitles;
    }

    private String checkForOptionalFlagOnAttributeName(AttributeLink attributeLink, Attribute attribute){
        return attributeLink.optional() ? attribute.name() : attribute.name() + "*";
    }

    private String checkForMultiValueFlagOnAttributeType(AttributeType attributeType){
        return attributeType.multiValue() ? attributeType.id() + "[]" : attributeType.id();
    }

    private String handleNullValue(String stringToCheck){
        return stringToCheck == null ? "" : stringToCheck;
    }
}
