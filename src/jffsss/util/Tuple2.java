package jffsss.util;

public class Tuple2<A, B>
{
	private A _A;
	private B _B;

	public Tuple2(A _A, B _B)
	{
		this._A = _A;
		this._B = _B;
	}

	public A getA()
	{
		return this._A;
	}

	public B getB()
	{
		return this._B;
	}
}