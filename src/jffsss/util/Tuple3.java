package jffsss.util;

public class Tuple3<A, B, C>
{
	private A _A;
	private B _B;
	private C _C;

	public Tuple3(A _A, B _B, C _C)
	{
		this._A = _A;
		this._B = _B;
		this._C = _C;
	}

	public A getA()
	{
		return this._A;
	}

	public B getB()
	{
		return this._B;
	}

	public C getC()
	{
		return this._C;
	}
}