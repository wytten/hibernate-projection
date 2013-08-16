package org.wyttenbach.dale.projection;

public interface Transform<S, D> {
  
  D doTransform(S src);

}

