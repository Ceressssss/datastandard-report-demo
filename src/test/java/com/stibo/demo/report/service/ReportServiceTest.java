package com.stibo.demo.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stibo.demo.report.model.Datastandard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ReportService.class, ObjectMapper.class})
public class ReportServiceTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ReportService reportService;

    private Datastandard datastandard;

    @BeforeEach
    public void before() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("datastandard.json");
        this.datastandard = objectMapper.readValue(stream, Datastandard.class);
    }

    @Test
    public void testReport() {
        Stream<Stream<String>> report = reportService.report(datastandard, "leaf");
        printBorder();
        report
                .forEach(rowStream -> {
                    List<String> cells = rowStream.toList();
                    String categoryName = String.format("%-" + 35 + "s", cells.get(0));
                    String attributeName = String.format("%-" + 35 + "s", cells.get(1));
                    String description = String.format("%-" + 35 + "s", cells.get(2));
                    String type = String.format("%-" + 35 + "s", cells.get(3));

                    String[] groups = cells.get(4).split("\n");
                    for (int i = 0; i < groups.length; i++) {
                        String group = String.format("%-" + 35 + "s", groups[i]);

                        if (i == 0) {
                            printRow(categoryName, attributeName, description, type, group);
                        } else {
                            // Create empty cell row to handle newline in group cell
                            printRow("", "", "", "", group);
                        }
                    }
                    printBorder();
                });
    }

    private static void printBorder() {
        String border = "+" + "-".repeat(35) + "+" + "-".repeat(35) + "+" + "-".repeat(35) + "+" + "-".repeat(35) + "+" + "-".repeat(35) + "+";
        System.out.println(border);
    }

    private static void printRow(String category, String attribute, String description, String type, String groups) {
        // Handling empty cell values in case of newlines
        category = category.isEmpty() ? String.format("%-" + 35 + "s",category) : category;
        attribute = attribute.isEmpty() ? String.format("%-" + 35 + "s",attribute) : attribute;
        description = description.isEmpty() ? String.format("%-" + 35 + "s",description) : description;
        type = type.isEmpty() ? String.format("%-" + 35 + "s",type) : type;
        groups = groups.isEmpty() ? String.format("%-" + 35 + "s",groups) : groups;

        String row = category + "|" + attribute + "|" + description + "|" + type + "|" + groups;
        System.out.println("|" + row + "|");
    }
}
