package netbeanstypescript;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.csl.api.*;
import org.netbeans.modules.csl.api.Error;

/**
 *
 * @author jeffrey
 */
public class TSHintsProvider implements HintsProvider {

    @Override
    public void computeHints(HintsManager manager, RuleContext context, List<Hint> hints) {
        System.out.println("TSHintsProvider#computeHints");
    }

    @Override
    public void computeSuggestions(HintsManager manager, RuleContext context, List<Hint> suggestions, int caretOffset) {
        System.out.println("TSHintsProvider#computeSuggestions");
    }

    @Override
    public void computeSelectionHints(HintsManager manager, RuleContext context, List<Hint> suggestions, int start, int end) {
        System.out.println("TSHintsProvider#computeSelectionHints");
    }

    @Override
    public void computeErrors(HintsManager manager, RuleContext context, List<Hint> hints, List<Error> unhandled) {
        System.out.println("TSHintsProvider#computeErrors");
        Map<?, List<? extends Rule.ErrorRule>> map = manager.getErrors();
        System.out.println(map);
        for (Error err: context.parserResult.getDiagnostics()) {
            System.out.println(err.getKey() + ": " + err.getDisplayName());
            boolean handled = false;
            List<? extends Rule.ErrorRule> rules = map.get(err.getKey());
            if (rules != null) {
                for (Rule.ErrorRule rule: rules) {
                    if (rule.appliesTo(context)) {
                        System.out.println("Applying rule: " + rule);
                        handled = true;
                        hints.add(new Hint(rule,
                                err.getDisplayName(),
                                context.parserResult.getSnapshot().getSource().getFileObject(),
                                new OffsetRange(err.getStartPosition(), err.getEndPosition()),
                                ((ImplementAllAbstractMethods) rule).getFixes(context),
                                0));
                    }
                }
            }
            if (! handled) {
                unhandled.add(err);
            }
        }
    }

    @Override
    public void cancel() {
        System.out.println("TSHintsProvider#cancel");
    }

    @Override
    public List<Rule> getBuiltinRules() {
        System.out.println("TSHintsProvider#getBuiltinRules");
        return null;
    }

    @Override
    public RuleContext createRuleContext() {
        System.out.println("TSHintsProvider#createRuleContext");
        return new RuleContext();
    }

    static class ImplementAllAbstractMethods implements Rule.ErrorRule {

        @Override
        public Set<?> getCodes() {
            return new HashSet<>(Arrays.asList("TS2515", "TS2653"));
        }

        @Override
        public boolean appliesTo(RuleContext rc) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return this.toString(); // ?
        }

        @Override
        public boolean showInTasklist() {
            return false; // ?
        }

        @Override
        public HintSeverity getDefaultSeverity() {
            return HintSeverity.ERROR;
        }

        List<HintFix> getFixes(final RuleContext context) {
            return Arrays.<HintFix>asList(new HintFix() {
                @Override
                public String getDescription() {
                    return "Implement all abstract methods";
                }
                @Override
                public void implement() throws Exception {
                    context.doc.insertString(0, "foo", null); // TODO
                }
                @Override
                public boolean isSafe() { return true; }
                @Override
                public boolean isInteractive() { return false; }
            });
        }
    }
}
