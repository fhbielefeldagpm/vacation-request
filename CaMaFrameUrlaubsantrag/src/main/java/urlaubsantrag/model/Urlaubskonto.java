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
package urlaubsantrag.model;

import javax.persistence.Entity;
import javax.persistence.Id;
/**
 * <p>
 * Class representing an employee's holiday account.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
public class Urlaubskonto {

	@Id
	private long antragstellerId; //employee id
	private int urlaubstage; // current days in account
	
	public Urlaubskonto() {
		
	}
	
	public Urlaubskonto(long antragstellerId, int urlaubstage) {
		this.antragstellerId = antragstellerId;
		this.urlaubstage = urlaubstage;
	}

	public long getAntragstellerId() {
		return antragstellerId;
	}

	public void setAntragstellerId(long antragestellerId) {
		this.antragstellerId = antragestellerId;
	}

	public int getUrlaubstage() {
		return urlaubstage;
	}

	public void setUrlaubstage(int urlaubstage) {
		this.urlaubstage = urlaubstage;
	}
	
}
