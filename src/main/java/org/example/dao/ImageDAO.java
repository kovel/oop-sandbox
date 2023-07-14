package org.example.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ImageDAO {
    private File file;

    public void setFile(String filepath) {
        this.file = new File(filepath);
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void create(List<String> args) {
        var imageUrl = args.get(0);
        try (var writer = new PrintWriter(new FileOutputStream(this.file, true))) {
            writer.printf("\n%s", imageUrl);
            writer.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> lines() {
        var lines = new LinkedList<String>();
        try (var scanner = new Scanner(this.file)) {
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                lines.add(scanner.next());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    public void delete(List<String> args) {
        var index = Integer.parseInt(args.get(0));
        var lines = new LinkedList<String>();

        try (var scanner = new Scanner(this.file)) {
            scanner.useDelimiter("\n");

            var pointer = 0;
            while (scanner.hasNext()) {
                var line = scanner.next();
                if (index != pointer) {
                    lines.add(line);
                }
                pointer++;
            }
            //System.out.println(lines);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            this.file.delete();
            this.file.createNewFile();

            try (var writer = new PrintWriter(new FileOutputStream(this.file, true))) {
                for (String line : lines) {
                    writer.printf("\n%s", line);
                    writer.flush();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
