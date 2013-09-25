package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class RulesFactoryTest {

    @Mock
    private AgeIsLessThanOneYearRule ageIsLessThanOneYearRule;
    @Mock
    private RelocationIsPermanentRule relocationIsPermanentRule;
    @Mock
    private CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule;
    @Mock
    private NewFPMethodIsCondomRule newFPMethodIsCondomRule;
    @Mock
    private MoreThanZeroCondomsSuppliedRule moreThanZeroCondomsSuppliedRule;
    @Mock
    private JsyBeneficiaryIsTrueRule jsyBeneficiaryIsTrueRule;
    @Mock
    private ThirdANCVisitHappenedOnTimeRule thirdANCVisitHappenedOnTimeRule;
    @Mock
    private ServiceProvidedAtSubCenterRule serviceProvidedAtSubCenterRule;
    @Mock
    private DeliveryHappenedAtHomeRule deliveryHappenedAtHomeRule;
    @Mock
    private DeliveryIsAttendedBySBATrainedPersonRule deliveryIsAttendedBySBATrainedPersonRule;

    private IRulesFactory rulesFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rulesFactory = new RulesFactory(ageIsLessThanOneYearRule,
                relocationIsPermanentRule,
                currentFPMethodIsCondomRule,
                newFPMethodIsCondomRule,
                moreThanZeroCondomsSuppliedRule,
                jsyBeneficiaryIsTrueRule,
                thirdANCVisitHappenedOnTimeRule,
                serviceProvidedAtSubCenterRule,
                deliveryIsAttendedBySBATrainedPersonRule,
                deliveryHappenedAtHomeRule);
    }

    @Test(expected = RuleNotFoundException.class)
    public void shouldThrowExceptionWhenRuleClassCannotBeFoundByName() throws Exception {
        String ruleName = "NonExistentRule";
        IRule rule = rulesFactory.ruleByName(ruleName);
    }

    @Test
    public void shouldLoadAgeIsLessThanOneYearRuleClassByName() throws Exception {
        String ruleName = "AgeIsLessThanOneYearRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof AgeIsLessThanOneYearRule);
    }

    @Test
    public void shouldLoadCurrentFPMethodIsCondomRuleClassByName() throws Exception {
        String ruleName = "CurrentFPMethodIsCondomRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof CurrentFPMethodIsCondomRule);
    }

    @Test
    public void shouldLoadRelocationIsPermanentRuleClassByName() throws Exception {
        String ruleName = "RelocationIsPermanentRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof RelocationIsPermanentRule);
    }

    @Test
    public void shouldLoadNewFPMethodIsCondomRuleClassByName() throws Exception {
        String ruleName = "NewFPMethodIsCondomRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof NewFPMethodIsCondomRule);
    }

    @Test
    public void shouldLoadMoreThanOneCondomSuppliedRuleRuleClassByName() throws Exception {
        String ruleName = "MoreThanZeroCondomsSuppliedRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof MoreThanZeroCondomsSuppliedRule);
    }


    @Test
    public void shouldLoadJsyBeneficiaryIsTrueRuleRuleClassByName() throws Exception {
        String ruleName = "JsyBeneficiaryIsTrueRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof JsyBeneficiaryIsTrueRule);
    }

    @Test
    public void shouldLoadThirdANCVisitHappenedOnTimeRuleClassByName() throws Exception {
        String ruleName = "ThirdANCVisitHappenedOnTimeRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof ThirdANCVisitHappenedOnTimeRule);
    }

    @Test
    public void shouldLoadServiceProvidedAtSubCenterRuleClassByName() throws Exception {
        String ruleName = "ServiceProvidedAtSubCenterRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof ServiceProvidedAtSubCenterRule);
    }

    @Test
    public void shouldLoadDeliveryIsAttendedBySBATrainedPersonRuleClassByName() throws Exception {
        String ruleName = "DeliveryIsAttendedBySBATrainedPersonRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof DeliveryIsAttendedBySBATrainedPersonRule);
    }

    @Test
    public void shouldLoadDeliveryHappenedAtHomeRuleClassByName() throws Exception {
        String ruleName = "DeliveryHappenedAtHomeRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof DeliveryHappenedAtHomeRule);
    }
}
