package paymenow.webapp.tests.integration

import static org.junit.Assert.*
import liquibase.exception.SetupException;

import com.grailsrocks.authentication.AuthenticationService;
import com.grailsrocks.authentication.AuthenticationUser
import org.junit.*

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static paymenow.webapp.matchers.HasErrors.*

import paymenow.webapp.domain.*
import paymenow.webapp.test.BaseFixtureLoader

class EmailAccountTests {
	
	
	def grailsApplication
	def baseFixtureLoader = new BaseFixtureLoader()
	def fixture

	@Before	
	void setUp() {
		fixture = baseFixtureLoader.load(grailsApplication)
	}
    @After
    void tearDown() {
        // Tear down logic here
    }
	
	@Test
	void happyCase(){
		
	}
	
	@Test
	void belongsToUser(){
		def email = EmailAccount.findByConfirmationCode(fixture.John.emails.toArray()[0].confirmationCode)
		fixture.John.delete(flush: true)
		assertThat(EmailAccount.findByConfirmationCode(email.confirmationCode),
			nullValue())
	}
	
	
}
