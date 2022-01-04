package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

import java.util.Objects;

public class CustomFurnitureBean {
    private String path;
    private String name;

    public CustomFurnitureBean(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CustomFurnitureBean{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomFurnitureBean that = (CustomFurnitureBean) o;

        if (!Objects.equals(path, that.path)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
