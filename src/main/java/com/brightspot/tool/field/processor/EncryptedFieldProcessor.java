package com.brightspot.tool.field.processor;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.utils.EncryptionUtils;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.State;
import org.apache.commons.lang3.StringUtils;

@WebServlet(EncryptedFieldProcessor.PATH)
public class EncryptedFieldProcessor extends PageServlet {

    public static final String PATH = "/_cms/field/encrypt";

    // -- Overrides -- //

    @Override
    protected String getPermissionId() {
        return null;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        HttpServletRequest request = page.getRequest();
        Object object = request.getAttribute("object");
        State state = State.getInstance(object);

        ObjectField field = (ObjectField) request.getAttribute("field");
        String fieldName = field.getInternalName();

        String inputName = (String) request.getAttribute("inputName");
        String formSubmissionInput = inputName + "/encryptedField";
        String formSubmissionValue = inputName + "/inputValue";

        String isNewValue = page.getRequest().getParameter(formSubmissionInput);
        if ((Boolean) request.getAttribute("isFormPost")) {
            if (Boolean.parseBoolean(isNewValue)) {
                String value = page.getRequest().getParameter(formSubmissionValue);
                if (StringUtils.isBlank(value)) {
                    state.addError(field, "Value can't be blank!");
                } else {
                    try {
                        state.put(fieldName, EncryptionUtils.encrypt(value));
                        state.put(fieldName + "ChangedDate", new Date());
                    } catch (Exception error) {
                        state.addError(field, error.getMessage());
                    }
                }
            }
        }

        page.writeStart("div", "class", "inputSmall");
        {
            page.writeStart("select", "class", "toggleable", "data-root", ".inputSmall", "id", page.getId(), "name", formSubmissionInput);
            {
                page.writeStart("option", "data-hide", ".valueChange", "value", false, "selected", "selected");
                {
                    page.writeHtml("Keep Same");
                }
                page.writeEnd();
                page.writeStart("option", "data-show", ".valueChange", "value", true);
                {
                    page.writeHtml("Change");
                }
                page.writeEnd();
            }
            page.writeEnd();

            page.writeStart("div", "class", "valueChange", "style", "margin-top: 10px;");
            {
                page.writeStart("div");
                page.writeHtml("New Value:");
                page.writeEnd();
                page.writeStart("input", "name", formSubmissionValue, "type", "password");
                page.writeEnd();
            }
            page.writeEnd();
        }
        page.writeEnd();
    }
}
