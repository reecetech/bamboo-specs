package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.support.JUnitResultHelper;
import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReeceSpecsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JUnitResultHelper resultHelper;

    private ReeceSpecs testInstance;

    @Before
    public void setupTest() {
        initMocks(this);

        testInstance = new ReeceSpecs(resultHelper);
    }

    @Test
    public void runFileProcess_invalidFile() {
        testInstance.runFileProcess(new SimpleUserPasswordCredentials("user", "pass"), true, "classpath:/permissions_invalid_yaml.yaml");

        verify(resultHelper).handleOutcome(any(RuntimeException.class), anyLong(), eq("classpath:/permissions_invalid_yaml.yaml"));
    }
}
