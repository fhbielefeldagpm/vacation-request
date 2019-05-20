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
package cm.core.utils;

import cm.core.rules.Rule;
import cm.core.rules.RuleExpression;
import cm.core.tasks.ProcessTask;
import cm.core.tasks.ProcessTaskImplementation;
import urlaubsantrag.impl.rules.RequiredRuleImpl;
/**
 * <p>
 * Central factory class with methods providing
 * {@link RuleExpression}s during runtime. A {@link Rule} is
 * used as a parameter. Its name is then used to get the correct implementation.
 * 
 * For example (rule being the given Rule as parameter): switch (rule.getName())
 * { case "repeatReview": return new RepeatReviewTask(rule);
 * 
 * </p>
 * 
 * @author André Zensen
 *
 */
public class RuleExpressionFactory {

	public static RuleExpression getExpressionForRule(Rule rule) {
		switch(rule.getName()) {
		case "required":
			return new RequiredRuleImpl(rule);
		default:
			return null;
		}
	}

	

}
