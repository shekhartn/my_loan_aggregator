package com.loan.aggregator.logmasking;

import java.util.Set;



public class PCIPIIProperties {
    private Set<String> mask;
    private Set<String> hash;
    private Set<String> remove;
	public Set<String> getMask() {
		return mask;
	}
	public void setMask(Set<String> mask) {
		this.mask = mask;
	}
	public Set<String> getHash() {
		return hash;
	}
	public void setHash(Set<String> hash) {
		this.hash = hash;
	}
	public Set<String> getRemove() {
		return remove;
	}
	public void setRemove(Set<String> remove) {
		this.remove = remove;
	}

    


}
