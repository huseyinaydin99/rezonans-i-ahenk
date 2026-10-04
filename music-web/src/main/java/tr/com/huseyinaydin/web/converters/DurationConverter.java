package tr.com.huseyinaydin.web.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("durationConverter")
public class DurationConverter implements Converter<Long> {

    @Override
    public Long getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0L;
        }
        try {
            String[] parts = value.split(":");
            long minutes = Long.parseLong(parts[0]);
            long seconds = Long.parseLong(parts[1]);
            return (minutes * 60) + seconds;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Long value) {
        if (value == null) {
            return "0:00";
        }
        long totalSeconds = value;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
