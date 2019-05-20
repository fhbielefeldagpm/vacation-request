/*
 * Copyright © 2018-2019 André Zensen, University of Applied Sciences Bielefeld
 * and various authors (see https://www.fh-bielefeld.de/wug/forschung/ag-pm)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package urlaubsantrag.impl.rules;

import cm.core.data.CaseFileItem;
import cm.core.rules.Rule;
import cm.core.rules.RuleExpression;

/**
 * Rule implementation for the required decorator on ProcessTask "Urlaubskonto
 * aktualisieren" ("update holiday account") (see {@link CaseFactory} blueprint).
 * 
 * @author André Zensen
 *
 */
public class RequiredRuleImpl extends RuleExpression {

	public RequiredRuleImpl(Rule rule) {
		super(rule);
	}

	@Override
	public boolean evaluate() {
		CaseFileItem caseFileItem = this.rule.getContextRef();
		if (Boolean.valueOf(caseFileItem.getProperty("required").getValue())) {
			return true;
		}
		return false;
	}

}
