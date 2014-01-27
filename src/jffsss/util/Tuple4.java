package jffsss.util;

public class Tuple4<A, B, C, D>
{
	private A _A;
	private B _B;
	private C _C;
	private D _D;

	public Tuple4(A _A, B _B, C _C, D _D)
	{
		this._A = _A;
		this._B = _B;
		this._C = _C;
		this._D = _D;
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

	public D getD()
	{
		return this._D;
	}
}