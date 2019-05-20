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
package cm.core.states;

import cm.core.CaseModel;

/**
 * <p>
 * Represents state CLOSED of {@link CaseModel}s. Used in the context of an
 * adapted state pattern. Implements permissible transitions.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.2, 6.3 and 8.2 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public class CaseInstanceClosed extends CaseInstanceState implements ICaseInstanceState {

	public CaseInstanceClosed(CaseModel element) {
		super(element);
	}

	// TODO Throw error / Message for logger for methods not applicable
	@Override
	public void create() {
	}

	@Override
	public void complete() {
	}

	@Override
	public void terminate() {
	}

	@Override
	public void fault() {
	}

	@Override
	public void suspend() {
	}

	@Override
	public void reActivate() {
	}

	@Override
	public void close() {
	}

}
