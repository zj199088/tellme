package com.fitness.app.dto;

import com.fitness.app.entity.Template;
import java.util.List;

public class GetTemplatesResponse {
    private List<Template> templates;

    public GetTemplatesResponse(List<Template> templates) {
        this.templates = templates;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }
}
