package de.randomwords;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import de.randomwords.eclipseignorehelper.EclipseIgnoreHelper;

public class EclipseIgnoreHelperTest extends EclipseIgnoreHelper {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException ee = ExpectedException.none();

    @Spy
    @InjectMocks
    private EclipseIgnoreHelper helper = new EclipseIgnoreHelper();

    @Mock
    private List<String> mockedIgnorePaths;

    @Mock
    private File mockedClassPathFile;

    /**
     * This is just a test for mockito.
     * 
     * @throws MojoExecutionException
     */
    @Test
    public void test() throws MojoExecutionException {
        ee.expect(MojoExecutionException.class);

        Mockito.when(mockedClassPathFile.exists()).thenReturn(true);
        helper.execute();
    }

}
