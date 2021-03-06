package paymenow.webapp.matchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.grails.datastore.mapping.validation.ValidationErrors;
import org.springframework.validation.BeanPropertyBindingResult;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.springframework.validation.FieldError;


import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class HasErrors extends BaseMatcher<DefaultGrailsDomainClass>  {

	
	private Map<String,String> errors;
	
	public HasErrors(Map<String, String> errorMap){
		this.errors = errorMap;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("Expected a domain object to fail on save() with errors: " + errors);
	}
	
	@Override
	public void describeMismatch(final Object item, final Description description){
		description.appendText("errors were").appendValue(createErrorsMap(item));
	}


	@Override
	public boolean matches(Object domain) {
		Map<String, String> errorMap = createErrorsMap(domain);
		assertThat(this.errors, equalTo(errorMap));
		return true;
	}

	private Map<String, String> createErrorsMap(Object domain) {
		InvokerHelper.invokeMethod(domain, "save", null);
		BeanPropertyBindingResult validationErrors = (BeanPropertyBindingResult)InvokerHelper.invokeMethod(domain, "getErrors", null);
		List<FieldError> errors = validationErrors.getFieldErrors();
		
		Map<String,String> errorMap = new HashMap<String,String>();
		for(FieldError err : errors){
			errorMap.put(err.getField(), validationErrors.getFieldError(err.getField()).getCode());
		}
		return errorMap;
	}
	
	@Factory
	public static Matcher<DefaultGrailsDomainClass> domainWithErrors(Map<String, String> errorMap) {
	    return new HasErrors(errorMap);
	}
	
	
}
